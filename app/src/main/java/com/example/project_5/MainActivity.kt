package com.example.project_5
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var characterImageView: ImageView
    private lateinit var characterNameTextView: TextView
    private lateinit var fetchCharacterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        characterImageView = findViewById(R.id.character_image_view)
        characterNameTextView = findViewById(R.id.character_name_text_view)
        fetchCharacterButton = findViewById(R.id.fetch_character_button)

        fetchCharacterButton.setOnClickListener {
            FetchCharacterTask().execute()
        }
    }

    private inner class FetchCharacterTask : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg params: Void?): JSONObject? {
            val randomId = (1..30).random()
            val url = URL("https://thronesapi.com/api/v2/Characters/$randomId")
            val urlConnection = url.openConnection() as HttpURLConnection

            try {
                val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val stringBuilder = StringBuilder()

                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line + "\n")
                }
                bufferedReader.close()

                val response = stringBuilder.toString()
                Log.d("API Response", response)

                return JSONObject(response)
            } catch (e: Exception) {
                Log.e("API Error", "Error fetching character", e)
            } finally {
                urlConnection.disconnect()
            }
            return null
        }

        override fun onPostExecute(result: JSONObject?) {
            if (result != null) {
                val name = result.getString("fullName")
                val imageUrl = result.getString("imageUrl")

                characterNameTextView.text = name
                Glide.with(this@MainActivity).load(imageUrl).into(characterImageView)
            } else {
                characterNameTextView.text = "Error fetching character"
            }
        }
    }}
