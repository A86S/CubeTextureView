package edu.a45.cubetextureview.utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by w1 on 12/11/2014.
 */

/**
 * Handle reading text source file
 */
public class TextResourceReader {
    /**
     * read text file from resource
     * @param context activity context
     * @param resourceId id of text resource
     * @return content of text file as String
     */
    public static String readTextFileFromResource(Context context, int resourceId){
        StringBuilder body = new StringBuilder();

        try{
            InputStream inputStream = context.getResources()
                    .openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream
            );
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;
            while((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append("\n");
            }
        }catch(IOException e){
            throw new RuntimeException(
                    "Could not open resource: "+resourceId, e
            );
        }catch(Resources.NotFoundException nfe){
            throw new RuntimeException(
                    "Resource not found: "+resourceId
                    ,nfe
            );
        }
        return body.toString();
    }
}
