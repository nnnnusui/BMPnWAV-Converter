package com.github.nnnnusui

import com.github.nnnnusui.io.binary.BMPTypeCORE
import com.github.nnnnusui.io.binary.WAV
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.concurrent.thread

fun main(){
    Application.launch(JavaFX::class.java)
//    Application.launch(HelloApp::class.java)
}
class JavaFX: Application(){
    override fun start(primaryStage: Stage) {
        primaryStage.show()
    }
}

class Hello: View(){
    val bmp = BMPTypeCORE()
    val wav = WAV()
    override val root = borderpane {
        center = splitpane(Orientation.VERTICAL) {
            vbox {
                label("plz. Drag&Drop the File.")
                label("AnyFile.xxx\t\t-> AnyFile.xxx.bmp"){styleClass.add("description")}
                label("AnyFile.xxx.bmp\t-> AnyFile_result.xxx"){styleClass.add("description")}
                borderpaneConstraints { marginLeftRight(10.0) }
                setConverterEvent { path, extension, data ->
                    if (extension == "bmp") { fromBMP(path, data) }
                    else                    { toBMP(  path, data) }
                }
            }
            vbox {
                add(label("plz. Drag&Drop the File."))
                add(label("AnyFile.xxx\t\t-> AnyFile.xxx.wav"){styleClass.add("description")})
                add(label("AnyFile.xxx.wav\t-> AnyFile_result.xxx"){styleClass.add("description")})
                borderpaneConstraints { marginLeftRight(10.0) }
                setConverterEvent { path, extension, data ->
                    if (extension == "wav") { fromWAV(path, data) }
                    else                    { toWAV(  path, data) }
                }
            }
        }
        bottom = label("...")
    }
    fun Pane.setConverterEvent(action: (path: Path, extension: String, data: ByteArray) -> Pair<Path, ByteArray>){
        setOnDragOver {
            if (it.gestureSource == this) return@setOnDragOver
            if (!it.dragboard.hasFiles()) return@setOnDragOver
            it.acceptTransferModes(TransferMode.COPY)
            it.consume()
        }
        setOnDragDropped {
            val files = it.dragboard.files.flatMap {
                            if (it.isDirectory) it.listFiles().toList()
                            else                 listOf(it)
                        }
            root.bottom = progressbar {
                thread { ;println("start convert.")
                    files.forEachIndexed { index, file ->
                        Platform.runLater { progress = index.toDouble() / files.size }
                        val path = file.toPath()
                        val extension = path.fileName.toString().substringAfterLast('.')
                        val byteArray = Files.readAllBytes(path)

                        val (outputPath
                            ,dataBinary) = action(path, extension, byteArray)
                        Files.write(outputPath, dataBinary)
                    }
                }
                root.bottom = label("complete.") ;println("complete.")
            }

        }
    }

    fun fromBMP(path: Path, data: ByteArray): Pair<Path, ByteArray>{
        val fileName  = path.fileName.toString().substringBeforeLast('.')
        val dataBinary = bmp.fromBMP(data)
        val originalExtension = if (fileName.contains('.'))
            fileName.substringAfterLast('.')
        else ""
        val originalFileName  = fileName.replace(".$originalExtension", "")

        val outputFileName = "${originalFileName}_result.$originalExtension"
        val outputPath = path.resolveSibling(Paths.get(outputFileName))
        return Pair(outputPath, dataBinary)
    }
    fun fromWAV(path: Path, data: ByteArray): Pair<Path, ByteArray>{
        val fileName  = path.fileName.toString().substringBeforeLast('.')
        val dataBinary = wav.fromWAV(data)
        val originalExtension = if (fileName.contains('.'))
            fileName.substringAfterLast('.')
        else ""
        val originalFileName  = fileName.replace(".$originalExtension", "")

        val outputFileName = "${originalFileName}_result.$originalExtension"
        val outputPath = path.resolveSibling(Paths.get(outputFileName))
        return Pair(outputPath, dataBinary)
    }
    fun toBMP(path: Path, byteArray: ByteArray): Pair<Path, ByteArray>{
        val bmpBinary = bmp.toBMP(byteArray)
        val outputPath = path.resolveSibling(Paths.get("${path.fileName}.bmp"))
        return Pair(outputPath, bmpBinary)
    }
    fun toWAV(path: Path, byteArray: ByteArray): Pair<Path, ByteArray>{
        val bmpBinary = wav.toWAV(byteArray)
        val outputPath = path.resolveSibling(Paths.get("${path.fileName}.wav"))
        return Pair(outputPath, bmpBinary)
    }
}

class HelloApp: App(Hello::class, Styles::class)
class Styles: Stylesheet(){
    companion object {
        val description by cssclass("description")
    }
    init {
        root {
            prefWidth = 600.px
            prefHeight = 600.px
        }
        box {
        }
        label {
            fontSize = 42.px
            fontWeight = FontWeight.BOLD
//            backgroundColor += c("#cecece")
            and(description) {
                fontSize = 22.px
                fontWeight = FontWeight.NORMAL
            }
        }
    }
}


