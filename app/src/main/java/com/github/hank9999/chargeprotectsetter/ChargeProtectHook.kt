package com.github.hank9999.chargeprotectsetter

import com.hchen.hooktool.BaseHC
import com.hchen.hooktool.hook.IHook
import com.hchen.hooktool.log.AndroidLog.logE
import com.hchen.hooktool.log.AndroidLog.logI
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.wrap.DexMethod
import java.lang.reflect.Method

class ChargeProtectHook : BaseHC() {

    companion object {
        lateinit var mDexKit: DexKitBridge
    }

    private var mAlwaysProtect: DexMethod? = null
    private var mChargeProtectionUtils: Method? = null
    private var mChargeProtectFragment: Method? = null
    private var mNotificationMethod: DexMethod? = null
    private var mNotificationTextMethodBaseBaseClass: Class<*>? = null
    private var mNotificationTextMethodBaseClass: Class<*>? = null
    private var mNotificationTextMethods: List<Method> = ArrayList()

    override fun init() {
        // 获取调用充电保护设置方法
        try {
            mAlwaysProtect = mDexKit.findClass {
                matcher {
                    usingStrings("BaseChargeProtect_AlwaysProtect")
                }
            }.findMethod {
                matcher {
                    usingStrings("openProtect AlwaysProtectManager")
                }
            }.singleOrNull()?.toDexMethod()

            logI(TAG, "mAlwaysProtect $mAlwaysProtect")
        } catch (e: Exception) {
            logE(TAG, "mAlwaysProtect $e")
        }

        if (mAlwaysProtect == null) {
            logE(TAG, "mAlwaysProtect is null, method not found")
            return
        }

        // 获取充电保护设置方法
        try {
            mChargeProtectionUtils = mDexKit.findClass {
                matcher {
                    usingStrings("ChargeProtectionUtils")
                }
            }.findMethod {
                matcher {
                    usingStrings("openCommonProtectMode:")
                }
            }.singleOrNull()?.getMethodInstance(classLoader)

            logI(TAG, "mChargeProtectionUtils $mChargeProtectionUtils")
        } catch (e: Exception) {
            logE(TAG, "mChargeProtectionUtils $e")
        }

        if (mChargeProtectionUtils == null) {
            logE(TAG, "mChargeProtectionUtils is null, method not found")
            return
        }

        // 修改充电保护参数
        hook(mChargeProtectionUtils, object : IHook() {
            override fun before() {
                if (!Thread.currentThread().isFromMethod(mAlwaysProtect!!)) {
                    return
                }
                for (i in 0..<argsLength()) {
                    if (getArgs(i).equals(80)) {
                        setArgs(i, 90)
                        break
                    }
                }
            }
        })

        // 获取充电保护 UI Fragment
        try {
            mChargeProtectFragment = findMethod(
                "com.miui.powercenter.nightcharge.ChargeProtectFragment",
                "onCreatePreferences",
                "android.os.Bundle",
                "java.lang.String"
            ).get()

            logI(TAG, "mChargeProtectFragment $mChargeProtectFragment")
        } catch (e: Exception) {
            logE(TAG, "mChargeProtectFragment $e")
        }

        if (mChargeProtectFragment == null) {
            logE(TAG, "mChargeProtectFragment is null, method not found")
            return
        }

        // 修改充电保护 UI 对应值
        hook(mChargeProtectFragment, object : IHook() {
            override fun after() {
                val instance = thisObject()
                instance.javaClass.declaredFields.forEach { field ->
                    if (field.type.name == "androidx.preference.CheckBoxPreference") {
                        field.isAccessible = true
                        val checkBoxPreference = field.get(instance)
                        if (callMethod(checkBoxPreference, "getKey").toString() == "cb_always_charge_protect") {
                            val newSummary = callMethod(checkBoxPreference, "getSummary").toString().replace("80%", "90%")
                            callMethod(checkBoxPreference, "setSummary", newSummary)
                        }
                        field.isAccessible = false
                    }
                }
            }
        })

        // 获取调用通知栏方法
        try {
            mNotificationMethod = mDexKit.findClass {
                matcher {
                    usingStrings("error playing ringtone ")
                    usingStrings("setExtremeSaveMode error")
                }
            }.findMethod {
                matcher {
                    paramTypes("android.content.Context")
                    usingNumbers(0.8, 0, 67108864, 4)
                    usingStrings("com.miui.powercenter.low")
                }
            }.singleOrNull()?.toDexMethod()

            logI(TAG, "mNotificationMethod $mNotificationMethod")
        } catch (e: Exception) {
            logE(TAG, "mNotificationMethod $e")
        }

        if (mNotificationMethod == null) {
            logE(TAG, "mNotificationMethod is null, method not found")
            return
        }

        // 获取通知栏方法类上面的类
        try {
            mNotificationTextMethodBaseBaseClass = mDexKit.findClass {
                matcher {
                    usingStrings("CommonNotification", "Params not support!", "com.miui.securitymanager")
                }
            }.singleOrNull()?.getInstance(classLoader)
            logI(TAG, "mNotificationTextMethodBaseBaseClass $mNotificationTextMethodBaseBaseClass")

        } catch (e: Exception) {
            logE(TAG, "mNotificationTextMethodBaseBaseClass $e")
        }

        if (mNotificationTextMethodBaseBaseClass == null) {
            logE(TAG, "mNotificationTextMethodBaseBaseClass is null, class not found")
            return
        }

        // 获取通知栏方法类
        if (mNotificationTextMethodBaseBaseClass?.classes?.size == 1) {
            mNotificationTextMethodBaseClass = mNotificationTextMethodBaseBaseClass?.classes?.get(0)
            logI(TAG, "mNotificationTextMethodBaseClass $mNotificationTextMethodBaseClass")
        } else {
            logE(TAG, "mNotificationTextMethodBaseBaseClass classes size is not 1")
            return
        }

        if (mNotificationTextMethodBaseClass == null) {
            logE(TAG, "mNotificationTextMethodBaseClass is null, class not found")
            return
        }

        // 获取通知栏方法
        try {
            mNotificationTextMethods = mDexKit.findClass {
                matcher {
                    className = mNotificationTextMethodBaseClass!!.name
                }
            }.findMethod {
                matcher {
                    paramTypes("java.lang.CharSequence")
                }
            }.map { it.getMethodInstance(classLoader) }

            logI(TAG, "mNotificationTextMethods $mNotificationTextMethods")
        } catch (e: Exception) {
            logE(TAG, "mNotificationTextMethodBaseClass $e")
        }

        if (mNotificationTextMethods.isEmpty()) {
            logE(TAG, "mNotificationTextMethods is empty, method not found")
            return
        }

        mNotificationTextMethods.forEach { method ->
            hook(method, object : IHook() {
                override fun before() {
                    if (!Thread.currentThread().isFromMethod(mNotificationMethod!!)) {
                        return
                    }

                    val summary = getArgs(0) as? CharSequence

                    if (summary == null) {
                        return
                    }
                    val newSummary = summary.toString().replace("80%", "90%") as CharSequence
                    setArgs(0, newSummary)
                }
            })
        }

        logI(TAG, "ChargeProtectHook success")
    }

    fun Thread.isFromMethod(method: DexMethod): Boolean {
        return this.stackTrace.any { it.methodName == method.name && it.className == method.className }
    }
}