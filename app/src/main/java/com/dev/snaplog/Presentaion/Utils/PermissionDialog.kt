import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.security.Permission

@Composable
fun PermissionDialog(
    permission: String,
    isPermDeclined: Boolean,
    onDismiss: () -> Unit,
    onOk: () -> Unit,
    onGoToSettingsClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (isPermDeclined){
                    onGoToSettingsClick.invoke()
                } else {
                    onOk.invoke()
                }
            }) {
                Text(text ="OK" )
            }
        },
        dismissButton = {},
        title = {
            Text(text = "Permission Required")
        },
        text = {
            Text("The app needs media permission to proceed")
        }

    )
}