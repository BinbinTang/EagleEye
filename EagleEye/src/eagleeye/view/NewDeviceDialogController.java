package eagleeye.view;

import java.io.File;

import eagleeye.entities.Device;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NewDeviceDialogController {

	private final String EMPTY_FIELD = "";
	
	@FXML
	private TextField deviceNameTxtBox;
	@FXML
	private TextField deviceOwnerTxtBox;
	@FXML
	private TextField filePathTxtBox;
	
	private boolean isImportClickedSuccess = false;
	private Stage dialogStage;
	private File file;
	private Device device;
	
	@FXML
	private void initiailize() {
		
	}
	
	public void setDialogStage(Stage dialogStage) {
		
		System.out.println("InDialogStageMethod");
		this.dialogStage = dialogStage;
	}
	
	public boolean isImportClickedSuccess() {
		
		return isImportClickedSuccess;
	}
	
	public Device getDevice() {
		
		return device;
	}
	
	private boolean isAnyFieldsEmpty() {
		
		if(deviceNameTxtBox.getText().equals(EMPTY_FIELD))
			return true;
		if(deviceOwnerTxtBox.getText().equals(EMPTY_FIELD))
			return true;
		if(filePathTxtBox.getText().equals(EMPTY_FIELD))
			return true;
		
		return false;
	}
	
	@FXML
	private void handleBrowse() {
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		file = directoryChooser.showDialog(null);
		filePathTxtBox.setText(file.getPath());
	}
	
	@FXML
	private void handleImport() {
		
		if(!isAnyFieldsEmpty()) {
			
			device = new Device();
			device.modifyDeviceName(deviceNameTxtBox.getText());
			device.modifyDeviceOwner(deviceOwnerTxtBox.getText());
			device.modifiyDeviceImageFolder(file);
			
			isImportClickedSuccess = true;
			dialogStage.close();
			
		}
	}
}




