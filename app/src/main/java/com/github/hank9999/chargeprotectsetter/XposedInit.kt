package com.github.hank9999.chargeprotectsetter

import com.hchen.hooktool.HCEntrance
import com.hchen.hooktool.HCInit
import com.hchen.hooktool.HCInit.BasicData
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.luckypray.dexkit.DexKitBridge

class XposedInit : HCEntrance() {
    override fun initHC(basicData: BasicData): BasicData {
        return basicData.setModulePackageName("com.github.hank9999.chargeprotectsetter")
            .setTag("ChargeProtectSetter")
            .setLogLevel(HCInit.LOG_D)
    }

    @Throws(Throwable::class)
    override fun onLoadPackage(lpparam: LoadPackageParam) {
        if ("com.miui.securitycenter" == lpparam.packageName) {
            HCInit.initLoadPackageParam(lpparam)
            init(lpparam.packageName, lpparam)
        }
    }

    fun init(pkg: String?, loadPackageParam: LoadPackageParam) {
        when (pkg) {
            "com.miui.securitycenter" -> {
                val hostDir = loadPackageParam.appInfo.sourceDir
                System.loadLibrary("dexkit")
                ChargeProtectHook.mDexKit = DexKitBridge.create(hostDir)
                ChargeProtectHook().onApplicationCreate().onLoadPackage()
                ChargeProtectHook.mDexKit.close()
            }
        }
    }
}
