package eagleeye.view;

import java.io.File;
import java.util.ArrayList;

import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.Device;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NewDeviceDialogController {

	private final String EMPTY_FIELD = "";
	private final String SAME_DEVICE_NAME_WARNING = "Device Name already existed";
	private final String EMPTY_DEVICE_NAME_WARNING = "Please Enter a Device Name";
	private final String EMPTY_OWNER_NAME_WARNING = "Please Enter Device Owner";
	private final String EMPTY_PATH_WARNING = "Please Specify the Image Path";
	
	@FXML
	private TextField deviceNameTxtBox;
	@FXML
	private TextField deviceOwnerTxtBox;
	@FXML
	private TextField filePathTxtBox;
	
	@FXML
	private Label deviceNameWarning;
	
	@FXML
	private Label ownerNameWarning;
	
	@FXML
	private Label pathWarning;
	
	private boolean isImportClickedSuccess = false;
	private Stage dialogStage;
	private File file;
	private Device device;
	private ArrayList<String> listOfDeviceNames;
	private DBQueryController dbController;
	
	public NewDeviceDialogController () {
		
		listOfDeviceNames = new ArrayList<String>();
		dbController = new DBQueryController();
		
		listOfDeviceNames = dbController.getAllDeviceNames();
	}
	
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
		
		boolean isAnyFieldsEmpty = false;
		
		if(deviceNameTxtBox.getText().equals(EMPTY_FIELD)) {
			deviceNameWarning.setText(EMPTY_DEVICE_NAME_WARNING);
			deviceNameWarning.setVisible(true);
			isAnyFieldsEmpty = true;
		}
			
		if(deviceOwnerTxtBox.getText().equals(EMPTY_FIELD)) {
			
			ownerNameWarning.setText(EMPTY_OWNER_NAME_WARNING);
			ownerNameWarning.setVisible(true);
			isAnyFieldsEmpty = true;
		}
		
		if(filePathTxtBox.getText().equals(EMPTY_FIELD)) {
			
			pathWarning.setText(EMPTY_PATH_WARNING);
			pathWarning.setVisible(true);
			isAnyFieldsEmpty = true;
		}
			
		
		return isAnyFieldsEmpty;
	}
	
	private boolean isDeviceNameExist(String newDeviceName) {
		
		for(String s : listOfDeviceNames) {
			
			if(newDeviceName.equals(s))
				return true;
			
		}
		
		return false;
	}
	
	@FXML
	private void handleBrowse() {
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		file = directoryChooser.showDialog(null);
		filePathTxtBox.setText(file.getPath());
	}
	
	@FXML
	private void deviceNameChange() {
		
		deviceNameWarning.setVisible(false);
	}
	
	@FXML
	private void ownerNameChange() {
		ownerNameWarning.setVisible(false);
	}
	
	@FXML
	private void pathChange() {
		pathWarning.setVisible(false);
	}
	
	@FXML
	private void handleImport() {
		
		if(!isAnyFieldsEmpty()) {
			
			if(isDeviceNameExist(deviceNameTxtBox.getText()))
			{
				deviceNameWarning.setText(SAME_DEVICE_NAME_WARNING);
				deviceNameWarning.setVisible(true);
			}
			else
			{
				device = new Device();
				device.modifyDeviceName(deviceNameTxtBox.getText());
				device.modifyDeviceOwner(deviceOwnerTxtBox.getText());
				device.modifiyDeviceImageFolder(file);
			
				isImportClickedSuccess = true;
				dialogStage.close();
			}
		}
		else
		{
			if(isDeviceNameExist(deviceNameTxtBox.getText()))
			{
				deviceNameWarning.setText(SAME_DEVICE_NAME_WARNING);
				deviceNameWarning.setVisible(true);
			}
		}
	}
}




