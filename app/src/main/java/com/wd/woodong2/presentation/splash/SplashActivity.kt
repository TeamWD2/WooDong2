package com.wd.woodong2.presentation.splash

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.wd.woodong2.R
import com.wd.woodong2.databinding.SplashActivityBinding
import com.wd.woodong2.databinding.SplashInAppUpdateProgressbarBinding
import com.wd.woodong2.presentation.signin.SignInActivity

class SplashActivity : AppCompatActivity() {

    private var _binding: SplashActivityBinding? = null
    private val binding get() = _binding!!

    /**
     * 업데이트 요청을 너무 자주 보낼 경우 사용자가 귀찮아할 수도 있으므로 업데이트 요청 횟수를 염두에 두어야 함
     * 앱의 핵심 기능에 중요한 변경인 경우에만 인앱 업데이트를 요청
     * 업데이트 내용에 따라 인앱 업데이트 코드 주석 필요
     *
     * version 5(1.4.0) - Flexible Update
     */

    private val appUpdateManager by lazy {
        // 인앱 업데이트 상태 추적 및 업데이트 시작
        AppUpdateManagerFactory.create(this)
    }

    // 업데이트 완료 후 앱 재시작 시 실행되는 콜백
    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.data == null) return@registerForActivityResult
        if (result.resultCode == FLEXIBLE_UPDATE_CODE) {
            if (result.resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "Downloading failed" , Toast.LENGTH_SHORT).show()
            } else {
                initView()
            }
        }
    }

    private val updateResultStarter =
        IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
            val request = IntentSenderRequest.Builder(intent)
                .setFillInIntent(fillInIntent)
                .setFlags(flagsValues, flagsMask)
                .build()
            updateLauncher.launch(request)
        }

    private var dialog: AlertDialog? = null

    // 업데이트 다운로드 상태 추적 리스너
    private val updateStateistener = InstallStateUpdatedListener { state ->
        // 업데이트 다운로드 상태가 변경될 때마다 호출
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            // 다운로드 진행 중 - 진행 상태 표시
            showProgressbarDialog(state)
        }
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // 다운로드 완료 - 사용자에게 알림 표시
            popupSnackbarForCompleteUpdate()
        }
        Log.i(TAG, "App Update status: ${state.installStatus()}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 업데이트 상태 감지 리스너 등록
        appUpdateManager.registerListener(updateStateistener)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // 앱 업데이트 시작
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateResultStarter,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                    FLEXIBLE_UPDATE_CODE
                )
            } else { // 유효한 업데이트가 없는 경우
                initView()
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "appUpdateInfoTask Failed : $exception")
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            }
        }
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

    private fun showProgressbarDialog(state: InstallState) {
        val bytesDownloaded = state.bytesDownloaded()
        val totalBytesToDownload = state.totalBytesToDownload()

        val dialogBinding = SplashInAppUpdateProgressbarBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(this)
            .setTitle(R.string.splash_dialog_download_progress_bar_title)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialog?.show()
        dialogBinding.progressBar.max = totalBytesToDownload.toInt()
        dialogBinding.progressBar.progress = bytesDownloaded.toInt()
    }

    private fun popupSnackbarForCompleteUpdate() {
        dialog?.dismiss()
        Snackbar.make(
            binding.root,
            R.string.splash_snack_bar_success_download,
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Restart") {
            appUpdateManager.completeUpdate()
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(updateStateistener) // 리스너 해제
    }

    companion object {
        const val SPLASH_DISPLAY_LENGTH = 2000L
        const val FLEXIBLE_UPDATE_CODE = 9999
        const val TAG = "SplashActivity"
    }
}