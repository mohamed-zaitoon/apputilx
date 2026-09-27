package com.mohamedzaitoon.apputilx

import com.mohamedzaitoon.apputilx.io.File as AppFile
import com.mohamedzaitoon.apputilx.io.Storage
import org.junit.Assert.assertEquals
import org.junit.Test

class FileTest {

    @Test
    fun testFileExtensionAndMimeType() {
        assertEquals("pdf", AppFile.getExtension("document.pdf"))
        assertEquals("png", AppFile.getExtension("image.PNG"))
        assertEquals("", AppFile.getExtension("noextension"))

        assertEquals("application/pdf", AppFile.getMimeType("document.pdf"))
        assertEquals("image/png", AppFile.getMimeType("photo.png"))
        assertEquals("text/plain", AppFile.getMimeType("notes.txt"))
        assertEquals("application/json", AppFile.getMimeType("data.json"))
        assertEquals("*/*", AppFile.getMimeType("file.unknown"))
    }

    @Test
    fun testFormatBytes() {
        assertEquals("0 B", Storage.formatBytes(0))
        assertEquals("1.00 KB", Storage.formatBytes(1024))
        assertEquals("1.00 MB", Storage.formatBytes(1024 * 1024))
        assertEquals("1.50 GB", Storage.formatBytes((1.5 * 1024 * 1024 * 1024).toLong()))
    }
}
