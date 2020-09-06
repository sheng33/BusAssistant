package com.shengq.notificationmanager

import android.app.AlertDialog
import com.xuexiang.xupdate.entity.PromptEntity
import com.xuexiang.xupdate.entity.UpdateEntity
import com.xuexiang.xupdate.proxy.IUpdatePrompter
import com.xuexiang.xupdate.proxy.IUpdateProxy
import com.xuexiang.xupdate.service.OnFileDownloadListener
import com.xuexiang.xupdate.utils.UpdateUtils
import com.xuexiang.xutil.display.HProgressDialogUtils
import java.io.File


class CustomUpdatePrompter : IUpdatePrompter {
    /**
     * 显示自定义提示
     *
     * @param updateEntity
     * @param updateProxy
     */
    private fun showUpdatePrompt(
        updateEntity: UpdateEntity,
        updateProxy: IUpdateProxy
    ) {
        val updateInfo =
            UpdateUtils.getDisplayUpdateInfo(updateProxy.context, updateEntity)
        val builder =
            AlertDialog.Builder(updateProxy.context)
                .setTitle(String.format("是否升级到%s版本？", updateEntity.versionName))
                .setMessage(updateInfo)
                .setPositiveButton(
                    "升级"
                ) { dialog, which ->
                    updateProxy.startDownload(updateEntity, object : OnFileDownloadListener {
                        override fun onStart() {
                            HProgressDialogUtils.showHorizontalProgressDialog(
                                updateProxy.context,
                                "下载进度",
                                false
                            )
                        }

                        override fun onProgress(
                            progress: Float,
                            total: Long
                        ) {
                            HProgressDialogUtils.setProgress(Math.round(progress * 100))
                        }

                        override fun onCompleted(file: File): Boolean {
                            HProgressDialogUtils.cancel()
                            return true
                        }

                        override fun onError(throwable: Throwable) {
                            HProgressDialogUtils.cancel()
                        }
                    })
                }
        if (updateEntity.isIgnorable) {
            builder.setNegativeButton(
                "暂不升级"
            ) { dialog, which ->
                UpdateUtils.saveIgnoreVersion(
                    updateProxy.context,
                    updateEntity.versionName
                )
            }.setCancelable(true)
        } else {
            builder.setCancelable(false)
        }
        builder.create().show()
    }

    /**
     * 显示版本更新提示
     *
     * @param updateEntity 更新信息
     * @param updateProxy  更新代理
     * @param promptEntity 提示界面参数
     */
    override fun showPrompt(
        updateEntity: UpdateEntity,
        updateProxy: IUpdateProxy,
        promptEntity: PromptEntity
    ) {
        showUpdatePrompt(updateEntity, updateProxy)
    }
}
