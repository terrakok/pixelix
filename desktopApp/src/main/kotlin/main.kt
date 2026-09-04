import com.daniebeler.pfpixelix.desktopApp

fun main(args: Array<String>) {
    System.setProperty("skiko.rendering.macos.metalSynchronousLiveResize", "true")
    System.setProperty("skiko.rendering.windows.direct3DSynchronousLiveResize", "true")
    desktopApp(args)
}
