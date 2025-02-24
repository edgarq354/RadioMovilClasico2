package com.elisoft.radiomovilclasico;

/**
 * Contiene las constantes de las acciones de los servicios y sus parámetros
 */
public class Constants {


    /**
     * Constantes para {@link Servicio_pedido}
     */
    public static final String ACTION_RUN_ISERVICE = "com.elisoft.radiomovilclasico.action.RUN_INTENT_SERVICE";
    public static final String ACTION_PROGRESS_EXIT = "com.elisoft.radiomovilclasico.action.PROGRESS_EXIT";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final int USE_ADDRESS_NAME = 1;
    public static final int USE_ADDRESS_LOCATION = 2;

    public static final String PACKAGE_NAME =
            "com.elisoft.radiomovilclasico";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_ADDRESS = PACKAGE_NAME + ".RESULT_ADDRESS";
    public static final String LOCATION_LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LATITUDE_DATA_EXTRA";
    public static final String LOCATION_LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LONGITUDE_DATA_EXTRA";
    public static final String LOCATION_NAME_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_NAME_DATA_EXTRA";
    public static final String FETCH_TYPE_EXTRA = PACKAGE_NAME + ".FETCH_TYPE_EXTRA";

    //inicio de buscador direccion
    public static final String API_NOT_CONNECTED = "Google API not connected";
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static String PlacesTag = "Google Places";
    //fin de buscador direccion

}
