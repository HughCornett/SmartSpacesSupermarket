package com.example.smartspacesblindshopping;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ReadWriteCSV {

    public static void writeToCSV(Context context, ArrayList<String> data, String path)
    {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(path, Context.MODE_APPEND));
            CSVWriter csvWriter = new CSVWriter(outputStreamWriter, CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);



            for(String s: data)
            {
                csvWriter.writeNext(new String[] {s});
            }
            csvWriter.close();
            outputStreamWriter.close();
        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ArrayList<String> readCSV(Context context, String path)
    {

        ArrayList<String> result = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(context.openFileInput(path));
            CSVReader csvReader = new CSVReader(reader);

            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null)
            {
                result.add(nextRecord[0]);
            }
            csvReader.close();
            reader.close();
        }
        catch (Exception e)
        {

        }

        return result;
    }

    public static void flush(Context context, String path) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(path, Context.MODE_PRIVATE));
            outputStreamWriter.close();
        } catch (Exception e) {

        }
    }

}
