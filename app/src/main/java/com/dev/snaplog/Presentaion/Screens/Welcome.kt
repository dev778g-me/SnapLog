import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dev.snaplog.Presentaion.Viewmodel.PermissionViewmodel
import com.dev.snaplog.Presentaion.navigation.Routes
import com.dev.snaplog.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Welcome(navController: NavHostController) {
    val permissionToRequest = arrayOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        },
        Manifest.permission.POST_NOTIFICATIONS
    )


 val viewmodel = viewModel<PermissionViewmodel>()
    val dialogQueue = viewmodel.visiablePermissionDialog
val context = LocalContext.current


    val multiplePermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
                perm ->
            var allgranted = true
            permissionToRequest.forEach { permission->
                val isGranted = perm[permission] == true
                viewmodel.onPermissionResult(
                    permission =permission,
                    isGranted = isGranted
                )
                if (!isGranted){
                    allgranted = false
                }
            }
if (allgranted){
    navController.navigate(Routes.Home.route) {
        popUpTo(Routes.WelcomeScreen.route){
            inclusive = true
        }
    }
}
        }
    )

    Scaffold (){
        paddingValues->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(paddingValues)
                .padding(16.dp)
        ) {
            Image(
                modifier = Modifier.align(Alignment.CenterHorizontally).size(400.dp),
                painter = painterResource(id = R.drawable.welcome, ),
                contentDescription = null
            )
            Text(
                text = "How the app works",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
                //textStyle = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your data is safe. No images are uploaded to the cloud. The text from images is extracted using OCR (ML) and is not stored anywhere. Only the AI's response is saved locally. Please grant all necessary permissions to get started",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))


            Row (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ){
                FilledTonalIconButton(onClick = {
                   multiplePermissions.launch(
                     permissionToRequest
                   )

                }, modifier = Modifier.fillMaxWidth()) {
                  //  Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
                    Text(text = "Allow permissions")
                }
            }
        }
        dialogQueue.reversed().forEach {
            perm->
            PermissionDialog(
                permission = "",

                isPermDeclined = !shouldShowRequestPermissionRationale(LocalActivity.current!!, perm),
                onDismiss = viewmodel::dismissDialog,
                onOk = {
                    viewmodel::dismissDialog.invoke()
                    multiplePermissions.launch(
                       arrayOf(perm)
                    )

                },
                onGoToSettingsClick = { context.startActivity(Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )) }

            )
        }
    }
}




fun Activity.openAppSettings (){
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)).also{startActivity(it)}
}