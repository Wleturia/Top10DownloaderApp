package leturiadev.com.top10downloader

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called")
        val downloadData = DownloadData()
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=10/xml")
        Log.d(TAG, "onCreate: done")
    }

    companion object {
        private class DownloadData : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"
            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute: parameters  is $result")
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                val xmlResult = StringBuilder()
                try {
                    val url = URL(urlPath)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(TAG, "downloadXML: The response code was $response")

                    connection.inputStream.buffered().reader().use {
                        xmlResult.append(it.readText())
                    }
                    Log.d(TAG, "Received ${xmlResult.length} bytes")
                    return xmlResult.toString()

                } catch (e: Exception) {
                    val errorMessage: String = when (e) {
                        is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
                        is IOException -> "downloadML: IO Exception reading data: ${e.message}"
                        is SecurityException -> {
                            e.printStackTrace()
                            "downloadXML: Invalid URL ${e.message}"
                        }
                        else -> "Unknown error: ${e.message}"
                    }
                }
                return "" //If its gets to here, there's been a problem. Return an empty String
            }
        }
    }


}
