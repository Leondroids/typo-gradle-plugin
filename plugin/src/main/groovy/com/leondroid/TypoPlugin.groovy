package com.leondroid

import org.gradle.api.Plugin
import org.gradle.api.Project

class TypoPlugin implements Plugin<Project> {
    def Project project

    def String tempFontDirectory
    def String assetDirectory

    void apply(Project project) {
        this.project = project
        project.extensions.create("typo", TypoPluginExtension)

        tempFontDirectory = "build/fonts/"
        assetDirectory = project.typo.fontDestination

        createTask(project.typo.robotoFont)
        createTask(project.typo.alexBrushFont)
        createTask(project.typo.pacificoFont)
        createTask(project.typo.ralewayFont)


        project.task('download-fonts') {
            if (project.typo.useRoboto) {
                dependsOn 'download-roboto'
            }

            if (project.typo.useAlexBrush) {
                dependsOn 'download-alex-brush'
            }

            if (project.typo.usePacifico) {
                dependsOn 'download-pacifico'
            }

            if (project.typo.useRaleway) {
                dependsOn 'download-raleway'
            }


            doLast {
                println "Cleaning up...."
                project.file(tempFontDirectory).deleteDir()
            }
        }
    }

    def createTask(Font font) {
        project.task('download-' + font.name) {
            inputs.property('font', font.name)
            outputs.dir(project.file(assetDirectory + "/" + font.name))

            doLast {
                downloadAndUnpackFont(font)
            }
        }
    }

    def downloadAndUnpackFont(Font font) {
        println "Downloading ${font.name} Font"
        def destFile = project.file(tempFontDirectory + font.archiveName)
        destFile.parentFile.mkdirs()

        new URL(font.source).withInputStream { i -> destFile.withOutputStream { it << i } }

        def ant = new groovy.util.AntBuilder()
        ant.unzip(src: destFile,
                dest: project.file(assetDirectory + font.name),
                overwrite: "false")

    }
}

class TypoPluginExtension {
    def String fontDestination = "src/main/assets/fonts/"
    def Font robotoFont = new Font(source: 'http://www.fontsquirrel.com/fonts/download/roboto', name: 'roboto', archiveName: 'roboto.zip')
    def Font alexBrushFont = new Font(source: 'http://www.fontsquirrel.com/fonts/download/alex-brush', name: 'alex-brush', archiveName: 'alex-brush.zip')
    def Font pacificoFont = new Font(source: 'http://www.fontsquirrel.com/fonts/download/pacifico', name: 'pacifico', archiveName: 'pacifico.zip')
    def Font ralewayFont = new Font(source: 'http://www.fontsquirrel.com/fonts/download/raleway', name: 'raleway', archiveName: 'raleway.zip')

    def Boolean useRoboto = true
    def Boolean useAlexBrush = true
    def Boolean useRaleway = true
    def Boolean usePacifico = true
}

class Font {
    def String source
    def String name
    def String archiveName
}


