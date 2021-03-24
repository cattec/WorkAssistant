package com.example.workassistant

import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


fun URL.getText(token_type: String, access_token: String): String {
    return openConnection().run {
        this as HttpURLConnection
        setRequestProperty("Authorization", token_type + ' ' + access_token);
        inputStream.bufferedReader().readText()
    }
}

fun URL.getText_noToken(): String {
    return openConnection().run {
        this as HttpURLConnection
        inputStream.bufferedReader().readText()
    }
}

fun URL.getIcon(token_type: String, access_token: String): InputStream {
    return openConnection().run {
        this as HttpURLConnection
        doInput = true
        setRequestProperty("Authorization", token_type + ' ' + access_token);
        inputStream
    }
}


fun URL.sendJSONRequest(token_type: String, access_token: String, outComment:String): String{
    return openConnection().run {
        this as HttpURLConnection
        doOutput = true
        doInput = true
        requestMethod = "POST"
        setRequestProperty("Authorization", token_type + ' ' + access_token);
        setRequestProperty("Content-Type", "application/json");
        setRequestProperty("Accept", "application/json");
        outputStream.write(outComment.toByteArray())
        outputStream.flush()
        if (responseCode != HttpURLConnection.HTTP_OK) {
            return "Can't post message!"
        }
        return "Send Message"
    }
}

fun URL.checkToken(token_type: String, access_token: String): String {
    return openConnection().run {
        this as HttpURLConnection
        requestMethod = "GET"
        setRequestProperty("Authorization", token_type + ' ' + access_token);

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return ""
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            return ""
        }

        inputStream.bufferedReader().readText()
    }
}

fun URL.getToken(myLogin: String, myPassword: String): String {
    return openConnection().run {
        this as HttpURLConnection
        doOutput = true
        doInput = true
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        setRequestProperty("Accept", "application/json");
        val responceData = "grant_type=&username=" + myLogin + "&password=" + myPassword + "&scope=&client_id=&client_secret="
        outputStream.write(responceData.toByteArray())
        outputStream.flush()

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return "\"you_error_detail\": \"Incorrect login or password!\""
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            return "\"you_error_detail\": \"You have some problem!\""
        }

        inputStream.bufferedReader().readText()
    }
}