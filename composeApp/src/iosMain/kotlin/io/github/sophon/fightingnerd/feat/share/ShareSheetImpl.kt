package io.github.sophon.fightingnerd.feat.share

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class ShareSheetImpl : ShareSheet {

    override suspend fun shareImage(pngBytes: ByteArray, fileName: String) {
        val fileUrl = withContext(Dispatchers.IO) {
            val path = NSTemporaryDirectory() + fileName
            val nsData = pngBytes.usePinned { pinned ->
                NSData.dataWithBytes(pinned.addressOf(0), pngBytes.size.toULong())
            }
            nsData.writeToFile(path, atomically = true)
            val url = NSURL.fileURLWithPath(path)
            url
        }

        val activityVc = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null,
        )
        UIApplication.sharedApplication.keyWindow
            ?.rootViewController
            ?.presentViewController(activityVc, animated = true, completion = null)
    }
}
