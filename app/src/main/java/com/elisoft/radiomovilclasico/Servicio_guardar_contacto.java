package com.elisoft.radiomovilclasico;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Servicio_guardar_contacto   extends IntentService {

String nombre="", telefono="";

    public Servicio_guardar_contacto() {
        super("Servicio_guardar_contacto");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bundle bundle=intent.getExtras();
            nombre=bundle.getString("nombre");
            telefono=bundle.getString("telefono");
            handleActionRun();

        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {

            cargar_contacto_empresa();
            // Quitar de primer plano
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public boolean buscar_contacto(String numero){
        boolean sw=false;

        Cursor crContacts;
        String TAG = "ContactsActivityTAG";


        Bitmap photo;
        String id, name, photoUri;
        String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        ContentResolver cr = getContentResolver();
        crContacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null, null, order);

        while (crContacts.moveToNext()&&sw==false) {

            id = crContacts.getString(crContacts.getColumnIndex(ContactsContract.Contacts._ID));
            name = crContacts.getString(crContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            if (Integer.parseInt(crContacts.getString(crContacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor crPhones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[]{id}, null);

                Log.d(TAG, "NAME: " + name);

                while (crPhones.moveToNext()&& sw==false) {
                    String phone = crPhones.getString(crPhones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.d(TAG, "\tPHONE: " + phone);

                    String value=phone;
                    value=value.replace(String.valueOf("+591 "),"");
                    value=value.replace(String.valueOf("+591"),"");
                    numero=numero.replace(String.valueOf("+591 "),"");
                    numero=numero.replace(String.valueOf("+591"),"");
                    if(value.equals(numero)==true|| value.equals("+591"+numero)==true) {
                        Log.d("contactos ", "columName: " + value);
                        sw=true;
                    }
                }
                crPhones.close();
            }

        }

        crContacts.close();
        return sw;
    }





    private void cargar_contacto_empresa() {


        String DisplayName = nombre+" -TAXI";
        String MobileNumber = "+591"+telefono;
        String HomeNumber = "+591"+telefono;
        String WorkNumber ="+591"+telefono;
        String emailID = "";
        String company = getString(R.string.app_name);
        String jobTitle = getString(R.string.app_name);


        if( buscar_contacto(MobileNumber)==false) {


            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            //------------------------------------------------------ Names
            if (DisplayName != null) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                DisplayName).build());
            }

            //------------------------------------------------------ Mobile Number
            if (MobileNumber != null) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
            }

            //------------------------------------------------------ Home Numbers
            if (HomeNumber != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        .build());
            }

            //------------------------------------------------------ Work Numbers
            if (WorkNumber != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Email
            if (emailID != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Organization
            if (!company.equals("") && !jobTitle.equals("")) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .build());
            }

            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }


}