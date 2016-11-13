package com.racdiseno.rutaepm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class ManejadorDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AppRutas.db";
    public static final int DATABASE_VERSION = 1;
    public static final String RUTAS_TABLE_NAME = "Rutas";
    public static final String STRING_TYPE = "text";
    public static final String INT_TYPE = "integer";

    //Campos de la tabla datos
    public static class Columndatos{
        public static final String ID_RUTAS = BaseColumns._ID;
        public static final String EMPRESA_RUTAS = "EMPRESA";
        public static final String RUTA_RUTAS = "RUTA";
        public static final String VERSION_RUTAS = "VERSION";
        public static final String MAPA_RUTAS = "MAPA";
        public static final String PARADAS_RUTAS = "PARADAS";
    }




    public ManejadorDB(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear la base de datos
          final String CREATE_CHEVY_SCRIPT =
                "create table "+RUTAS_TABLE_NAME+"(" +
                        Columndatos.ID_RUTAS+" "+INT_TYPE+" primary key autoincrement," +
                        Columndatos.EMPRESA_RUTAS +" "+STRING_TYPE+" not null," +
                        Columndatos.RUTA_RUTAS+" "+STRING_TYPE+" not null," +
                        Columndatos.VERSION_RUTAS+" "+STRING_TYPE+" not null," +
                        Columndatos.MAPA_RUTAS+" "+STRING_TYPE+" not null," +
                        Columndatos.PARADAS_RUTAS+" "+STRING_TYPE+")";

        db.execSQL(CREATE_CHEVY_SCRIPT);


        //Insertar registros iniciales
        //db.execSQL(QuotesDataSource.INSERT_QUOTES_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Actualizar la base de datos
        db.execSQL("DROP TABLE IF EXISTS "+RUTAS_TABLE_NAME);
        onCreate(db);
    }


    public void insertar(String EMPRESA,String RUTA,String VERSION,String MAPA,String PARADAS){
        //Nuestro contenedor de valores
        ContentValues values = new ContentValues();

        //Seteando body y author
        values.put(Columndatos.EMPRESA_RUTAS,EMPRESA);
        values.put(Columndatos.RUTA_RUTAS,RUTA);
        values.put(Columndatos.VERSION_RUTAS, VERSION);
        values.put(Columndatos.MAPA_RUTAS, MAPA);
        values.put(Columndatos.PARADAS_RUTAS, PARADAS);

        //Insertando en la base de datos
        this.getWritableDatabase().insert(RUTAS_TABLE_NAME, null, values);
        //db.insert(CHEVY_TABLE_NAME, null, values);
    }

    public void actualizar(String EMPRESA,String RUTA,String VERSION,String MAPA,String PARADAS){
        //Nuestro contenedor de valores
        ContentValues values = new ContentValues();

        //Seteando body y author
        values.put(Columndatos.EMPRESA_RUTAS,EMPRESA);
        values.put(Columndatos.RUTA_RUTAS,RUTA);
        values.put(Columndatos.VERSION_RUTAS, VERSION);
        values.put(Columndatos.MAPA_RUTAS, MAPA);
        values.put(Columndatos.PARADAS_RUTAS, PARADAS);

        //Actualizando en la base de datos
        //TODO rutina UPDATE en BD
        //this.getWritableDatabase().update(RUTAS_TABLE_NAME, values,);
    }



    public Cursor leertodo(){

        String Result = "";
        String Columnas[] = {Columndatos.ID_RUTAS, Columndatos.EMPRESA_RUTAS, Columndatos.RUTA_RUTAS, Columndatos.VERSION_RUTAS, Columndatos.MAPA_RUTAS, Columndatos.PARADAS_RUTAS};
        Cursor c = this.getReadableDatabase().query(RUTAS_TABLE_NAME, Columnas, null, null, null, null, null);
        return c;

    }


    public void abrir(){
        this.getWritableDatabase();
    }

    public void cerrar (){
        this.close();
    }

    }


