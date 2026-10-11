import com.daniebeler.pfpixelix.desktopApp

fun main(args: Array<String>) {
    //smooth window resizing on windows and macos
    System.setProperty("skiko.rendering.macos.metalSynchronousLiveResize", "true")
    System.setProperty("skiko.rendering.windows.direct3DSynchronousLiveResize", "true")
    desktopApp(args)
}
