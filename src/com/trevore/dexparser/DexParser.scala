package com.trevore.dexparser

import java.io.{DataInputStream, InputStream}
import java.nio.file.{FileSystems, Files}

/**
 * Created by trevor on 6/5/15.
 */
class DexParser {

  private class LittleEndianDataInputStream(in: InputStream) extends DataInputStream(in) {
    def readIntLE() = Integer.reverseBytes(readInt())
    def readShortLE() = java.lang.Short.reverseBytes(readShort())
    def readUnsignedLeb128(): Int = {
      var result = 0
      var byte = 0
      do {
        byte = readByte()
        result = (result << 7) | (byte & 0x7F)
      } while (byte < 0)
      result
    }
  }

  def load(filePath: String): Unit = {
    val path = FileSystems.getDefault().getPath(filePath)
    val dataInputStream = new LittleEndianDataInputStream(Files.newInputStream(path))

    println("Parsing DEX file...")
    parseHeader(dataInputStream)
    println("Parsing finished!")
  }

  private val DEX_FILE_MAGIC = Array[Byte](0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x35, 0x00)
  private val ENDIAN_CONSTANT = 0x12345678
  private val REVERSE_ENDIAN_CONSTANT = 0x78563412
  private val NO_INDEX = 0xffffffff

  def parseHeader(inputStream: LittleEndianDataInputStream): Unit = {
    val bytes = new Array[Byte](8)
    for (x <- 0 to bytes.length - 1) {
      bytes(x) = inputStream.readByte()
    }

    if (!bytes.sameElements(DEX_FILE_MAGIC))
      throw new IllegalArgumentException("DEX_FILE_MAGIC did not match")

    inputStream.skipBytes(4 + 20) // Don't care about the checksum and signature
    val header = Header(inputStream.readIntLE(), inputStream.readIntLE(), parseEndianTag(inputStream),
      inputStream.readIntLE(), inputStream.readIntLE(), inputStream.readIntLE(),
      inputStream.readIntLE(), inputStream.readIntLE(), inputStream.readIntLE(),
      inputStream.readIntLE(), inputStream.readIntLE(), inputStream.readIntLE(),
      inputStream.readIntLE(), inputStream.readIntLE(), inputStream.readIntLE(),
      inputStream.readIntLE(), inputStream.readIntLE(), inputStream.readIntLE(),
      inputStream.readIntLE(), inputStream.readIntLE())
    println(header)
    println(header.methodIdsSize)
  }

  def parseEndianTag(inputStream: LittleEndianDataInputStream): Boolean = {
    val endianTag = inputStream.readIntLE()
    if (endianTag == ENDIAN_CONSTANT)
      return true
    else if (endianTag == REVERSE_ENDIAN_CONSTANT)
      return false
    else
      throw new IllegalArgumentException("Unknown endian")
  }

  case class Header(fileSize: Int, headerSize: Int, isLittleEndian: Boolean,
                     linkSize: Int, linkOffset: Int, mapOffset: Int,
                     stringIdsSize: Int, stringIdsOffset: Int, typeIdsSize: Int,
                     typeIdsOffset: Int, protoIdsSize: Int, protoIdsOffset: Int,
                     fieldIdsSize: Int, fieldIdsOffset: Int, methodIdsSize: Int,
                     methodIdsOffset: Int, classDefsSize: Int, classDefsOffset: Int,
                     dataSize: Int, dataOffset: Int)

}
