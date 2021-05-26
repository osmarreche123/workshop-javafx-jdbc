package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.entities.Seller;
import models.exceptions.ValidationException;
import models.services.SellerService;

import java.lang.invoke.ConstantBootstraps;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {


    private Seller entity;

    private SellerService service;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private DatePicker dpBirthDate;

    @FXML
    private TextField textFieldBaseSalary;

    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;


    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    @FXML
    public void onButtonSaveAction(ActionEvent event){

        if (entity == null){
            throw new IllegalStateException("Entity was null");
        }

        if (service == null){
            throw new IllegalStateException("Service was null");
        }

        try{
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        }
        catch (ValidationException e){
            setErrormessages(e.getErrors());
        }
        catch (DbException e){
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);

        }


    }



    @FXML
    public void onButtonCancelAction(ActionEvent action){
        Utils.currentStage(action).close();
    }

    private Seller getFormData() {

        Seller obj = new Seller();

        ValidationException exception = new ValidationException("Validation Erro");

        obj.setId(Utils.tryParseToInt(textFieldId.getText()));

        if(textFieldName.getText() == null || textFieldName.getText().trim().equals("")){
            exception.addError("name","field can't by empty");
        }
        obj.setName(textFieldName.getText());

        if (exception.getErrors().size() > 0){
            throw exception;
        }
        return obj;
    }

    public void setSeller(Seller entity){
        this.entity = entity;
    }

    public void setSellerService(SellerService service) {
        this.service = service;
    }

    public void subscribeDataChangeListeners(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeNodes();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 70);
        Constraints.setTextFielDouble(textFieldBaseSalary);
        Constraints.setTextFieldMaxLength(textFieldEmail, 60);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
    }

    public void updateFormData(){

        if (entity == null){
            throw new IllegalStateException("Entity was null");
        }
        textFieldId.setText(String.valueOf(entity.getId()));
        textFieldName.setText(entity.getName());
        textFieldEmail.setText(entity.getEmail());
        Locale.setDefault(Locale.US);
        textFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        if (entity.getBirthDate() != null){
            dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }


    }

    private void setErrormessages(Map<String,String> errors){
        Set<String> fields = errors.keySet();

        if (fields.contains("name")){
            labelErrorName.setText(errors.get("name"));
        }
    }


}
