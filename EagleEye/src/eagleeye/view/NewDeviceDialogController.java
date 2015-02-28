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
			deviceNameTxtBox.getStyleClass().add("custom-wrong-textField");
			isAnyFieldsEmpty = true;
		}
			
		if(deviceOwnerTxtBox.getText().equals(EMPTY_FIELD)) {
			
			ownerNameWarning.setText(EMPTY_OWNER_NAME_WARNING);
			ownerNameWarning.setVisible(true);
			deviceOwnerTxtBox.getStyleClass().add("custom-wrong-textField");
			isAnyFieldsEmpty = true;
		}
		
		if(filePathTxtBox.getText().equals(EMPTY_FIELD)) {
			
			pathWarning.setText(EMPTY_PATH_WARNING);
			pathWarning.setVisible(true);
			filePathTxtBox.getStyleClass().add("custom-wrong-textField");
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
		File f = directoryChooser.showDialog(null);
		if(f==null) return;
		String filePath = f.getAbsolutePath();
		filePathTxtBox.setText(filePath);
	}
	
	@FXML
	private void deviceNameChange() {
		
		deviceNameWarning.setVisible(false);
		deviceNameTxtBox.getStyleClass().remove("custom-wrong-textField");
	}
	
	@FXML
	private void ownerNameChange() {
		ownerNameWarning.setVisible(false);
		deviceOwnerTxtBox.getStyleClass().remove("custom-wrong-textField");
	}
	
	@FXML
	private void pathChange() {
		pathWarning.setVisible(false);
		filePathTxtBox.getStyleClass().remove("custom-wrong-textField");
	}
	
	@FXML
	private void handleImport() {
		
		if(!isAnyFieldsEmpty()) {
			
			if(isDeviceNameExist(deviceNameTxtBox.getText()))
			{
				deviceNameWarning.setText(SAME_DEVICE_NAME_WARNING);
				deviceNameWarning.setVisible(true);
				deviceNameTxtBox.getStyleClass().add("custom-wrong-textField");
				
			}
			else
			{
				device = new Device();
				device.modifyDeviceName(deviceNameTxtBox.getText());
				device.modifyDeviceOwner(deviceOwnerTxtBox.getText());
				device.modifiyDeviceImageFolder(new File(filePathTxtBox.getText()));
			
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




