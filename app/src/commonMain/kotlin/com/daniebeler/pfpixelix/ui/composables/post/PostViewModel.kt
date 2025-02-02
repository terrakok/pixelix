package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.usecase.*
import com.daniebeler.pfpixelix.ui.composables.post.reply.OwnReplyState
import com.daniebeler.pfpixelix.ui.composables.post.reply.RepliesState
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.TimeAgo
import com.daniebeler.pfpixelix.utils.zestDir
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject


class PostViewModel @Inject constructor(
    private val getRepliesUseCase: GetRepliesUseCase,
    private val createReplyUseCase: CreateReplyUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val reblogPostUseCase: ReblogPostUseCase,
    private val unreblogPostUseCase: UnreblogPostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase,
    private val unbookmarkPostUseCase: UnbookmarkPostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val currentLoginDataUseCase: GetCurrentLoginDataUseCase,
    private val getAccountsWhoLikedPostUseCase: GetAccountsWhoLikedPostUseCase,
    private val getHideAltTextButtonUseCase: GetHideAltTextButtonUseCase,
    private val openExternalUrlUseCase: OpenExternalUrlUseCase,
    private val getVolumeUseCase: GetVolumeUseCase,
    private val setVolumeUseCase: SetVolumeUseCase
) : ViewModel() {

    var post: Post? by mutableStateOf(null)

    var repliesState by mutableStateOf(RepliesState())

    var ownReplyState by mutableStateOf(OwnReplyState())

    var likedByState by mutableStateOf(LikedByState())

    var deleteState by mutableStateOf(DeleteState())
    var deleteDialog: String? by mutableStateOf(null)
    var timeAgoString: String by mutableStateOf("")

    var showPost: Boolean by mutableStateOf(false)

    var myAccountId: String? = null

    var isAltTextButtonHidden by mutableStateOf(false)

    var volume by mutableStateOf(false)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            myAccountId = currentLoginDataUseCase()!!.accountId
        }

        viewModelScope.launch {
            getHideAltTextButtonUseCase().collect { res ->
                isAltTextButtonHidden = res
            }
        }
    }

    fun toggleVolume(newVolume: Boolean) {
        volume = newVolume
        viewModelScope.launch {
            setVolumeUseCase(newVolume)
        }
    }

    fun updatePost(post: Post) {
        this.post = post
        getVolume()
    }

    private fun getVolume() {
        viewModelScope.launch {
            getVolumeUseCase().collect { res ->
                volume = res
            }
        }
    }

    fun deletePost(postId: String) {
        deleteDialog = null
        deletePostUseCase(postId).onEach { result ->
            deleteState = when (result) {
                is Resource.Success -> {
                    DeleteState(deleted = true)
                }

                is Resource.Error -> {
                    DeleteState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    DeleteState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleShowPost() {
        showPost = !showPost
    }

    fun convertTime(createdAt: String) {
        timeAgoString = TimeAgo.convertTimeToText(createdAt)
    }

    fun loadReplies(postId: String) {
        getRepliesUseCase(postId).onEach { result ->
            repliesState = when (result) {
                is Resource.Success -> {
                    RepliesState(replies = result.data?.descendants ?: emptyList())
                }

                is Resource.Error -> {
                    RepliesState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    RepliesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createReply(postId: String, commentText: String) {
        if (commentText.isNotEmpty()) {
            createReplyUseCase(postId, commentText).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        ownReplyState = OwnReplyState(reply = result.data)
                        loadReplies(postId)
                    }

                    is Resource.Error -> {
                        ownReplyState =
                            OwnReplyState(error = result.message ?: "An unexpected error occurred")
                    }

                    is Resource.Loading -> {
                        ownReplyState = OwnReplyState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteReply(postId: String) {
        deletePostUseCase(postId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    repliesState =
                        repliesState.copy(replies = repliesState.replies.filter { it.id != postId })
                }

                is Resource.Error -> {
                    println(result.message)
                }

                is Resource.Loading -> {
                    println("is loading")
                }
            }
        }.launchIn(viewModelScope)
    }


    fun loadLikedBy(postId: String) {
        getAccountsWhoLikedPostUseCase(postId).onEach { result ->
            likedByState = when (result) {
                is Resource.Success -> {
                    LikedByState(likedBy = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    LikedByState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    LikedByState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun likePost(postId: String) {
        if (post?.favourited == false) {
            post = post?.copy(
                favourited = true, favouritesCount = post?.favouritesCount?.plus(
                    1
                ) ?: 0
            )
            CoroutineScope(Dispatchers.Default).launch {
                likePostUseCase(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(
                                favourited = result.data?.favourited ?: true, favouritesCount = result.data?.favouritesCount ?: 0
                            )
                        }

                        is Resource.Error -> {
                            post = post?.copy(favourited = false, favouritesCount = result.data?.favouritesCount?.minus(1) ?: 0)
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun unlikePost(postId: String) {
        if (post?.favourited == true) {
            CoroutineScope(Dispatchers.Default).launch {
                unlikePostUseCase(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(favourited = result.data?.favourited ?: false)
                        }

                        is Resource.Error -> {
                            post = post?.copy(favourited = true)
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun reblogPost(postId: String) {
        if (post?.reblogged == false) {
            post = post?.copy(reblogged = true)
            CoroutineScope(Dispatchers.Default).launch {
                reblogPostUseCase(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(reblogged = result.data?.reblogged ?: false)
                        }

                        is Resource.Error -> {
                            post = post?.copy(reblogged = false)
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun unreblogPost(postId: String) {
        if (post?.reblogged == true) {
            CoroutineScope(Dispatchers.Default).launch {
                unreblogPostUseCase(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(reblogged = result.data?.reblogged ?: false)
                        }

                        is Resource.Error -> {
                            post = post?.copy(reblogged = true)
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun bookmarkPost(postId: String) {
        if (post?.bookmarked == false) {
            post = post?.copy(bookmarked = true)
            CoroutineScope(Dispatchers.Default).launch {
                bookmarkPostUseCase(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(bookmarked = result.data?.bookmarked ?: false)
                        }

                        is Resource.Error -> {
                            post = post?.copy(bookmarked = false)
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun unBookmarkPost(postId: String) {
        if (post?.bookmarked == true) {
            post = post?.copy(bookmarked = false)
            CoroutineScope(Dispatchers.Default).launch {
                unbookmarkPostUseCase(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(bookmarked = result.data?.bookmarked ?: false)
                        }

                        is Resource.Error -> {
                            post = post?.copy(bookmarked = true)
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }

    }

    fun openUrl(context: KmpContext, url: String) {
        openExternalUrlUseCase(context, url)
    }


    private fun generateUniqueName(
        imageName: String?, returnFullPath: Boolean, context: KmpContext
    ): String {
        val filename = imageName + "_" + Clock.System.now().epochSeconds
        if (returnFullPath) {
            return context.zestDir.resolve(filename).toString()
        } else {
            return filename
        }
    }

    fun saveImage(name: String?, url: String, context: KmpContext) {
        //todo
//        var uri: Uri? = null
//        val saveImageRoutine = CoroutineScope(Dispatchers.Default).launch {
//
//            val bitmap: Bitmap? = urlToBitmap(url, context)
//            if (bitmap == null) {
//                cancel("an error occured when downloading the image")
//                return@launch
//            }
//
//            println(bitmap.toString())
//
//            uri = saveImageToMediaStore(context, generateUniqueName(name, false, context), bitmap!!)
//            if (uri == null) {
//                cancel("an error occured when saving the image")
//                return@launch
//            }
//        }
//
//        saveImageRoutine.invokeOnCompletion { throwable ->
//            CoroutineScope(Dispatchers.Main).launch {
//                uri?.let {
//                    Toast.makeText(context, "Stored at: " + uri.toString(), Toast.LENGTH_LONG)
//                        .show()
//                } ?: throwable?.let {
//                    Toast.makeText(
//                        KmpContext, "an error occurred downloading the image", Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }
    }

//    private suspend fun urlToBitmap(
//        imageURL: String,
//        context: KmpContext,
//    ): Bitmap? {
//        val loader = ImageLoader(context)
//        val request = ImageRequest.Builder(context).data(imageURL).allowHardware(false).build()
//        val result = loader.execute(request)
//        if (result is SuccessResult) {
//            return result.image.toBitmap()
//        }
//        return null
//    }
//
//    private fun saveImageToMediaStore(context: KmpContext, displayName: String, bitmap: Bitmap): Uri? {
//        val imageCollections = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//        } else {
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        }
//
//        val imageDetails = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
//            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                put(MediaStore.Images.Media.IS_PENDING, 1)
//            }
//        }
//
//        val resolver = KmpContext.applicationContext.contentResolver
//        val imageContentUri = resolver.insert(imageCollections, imageDetails) ?: return null
//
//        return try {
//            resolver.openOutputStream(imageContentUri, "w").use { os ->
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os!!)
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                imageDetails.clear()
//                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
//                resolver.update(imageContentUri, imageDetails, null, null)
//            }
//
//            imageContentUri
//        } catch (e: FileNotFoundException) {
//            // Some legacy devices won't create directory for the Uri if dir not exist, resulting in
//            // a FileNotFoundException. To resolve this issue, we should use the File API to save the
//            // image, which allows us to create the directory ourselves.
//            null
//        }
//    }
}