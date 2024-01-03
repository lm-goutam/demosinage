import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecAction
import org.gradle.process.internal.ExecActionFactory

import javax.inject.Inject

class ExtractDexTask extends DefaultTask {

    @Input
    String androidSDKLocation;
    @Input
    String dexName;
    @Input
    String buildToolsVersion;
    @Input String srcName;

    @Input
    String srcFolder = "build/outputs/aar/"
    @Input
    String srcVariant = "debug"
    @Input
    String srcPkgType = "aar"
    @Input
    String destinationFolder = "${project.rootDir}/LemmaSignageWSDK/src/main/res/raw"

    @TaskAction
    def extractDex() {
        print("extractDex ------> ")
        unzipPkg()
//        createClassesX()
        dexClassFile()
//        dexXClassFile()
    }

    def unzipPkg() {
        getProject().copy {
            from getProject().zipTree("${srcFolder}/${srcName}-${srcVariant}.${srcPkgType}")
            into "${srcFolder}/${srcName}"
        }
    }

    def createClassesX(){
        ExecAction execAction = getExecActionFactory().newExecAction()
        execAction.setExecutable("./jetifier-standalone/bin/jetifier-standalone")
        execAction.setArgs(["-i", "${srcFolder}/${srcName}/classes.jar",
                            "-o", "${srcFolder}/${srcName}/classesx.jar"])
        execAction.execute()
    }

    def dexClassFile() {
        ExecAction execAction = getExecActionFactory().newExecAction()
        execAction.setExecutable("${androidSDKLocation}/build-tools/${buildToolsVersion}/dx")
        execAction.setArgs(["--dex", "--output",
                "${destinationFolder}/${dexName}.dex",
                "${srcFolder}/${srcName}/classes.jar"])
        execAction.execute()
    }

    def dexXClassFile() {
        ExecAction execAction = getExecActionFactory().newExecAction()
        execAction.setExecutable("${androidSDKLocation}/build-tools/${buildToolsVersion}/dx")
        execAction.setArgs(["--dex", "--output",
                            "${destinationFolder}/${dexName}x.dex",
                            "${srcFolder}/${srcName}/classesx.jar"])
        execAction.execute()
    }
    @Inject
    protected ExecActionFactory getExecActionFactory() {
        throw new UnsupportedOperationException()
    }
}