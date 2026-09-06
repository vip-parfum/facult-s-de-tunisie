package com.example.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.Faculty

object IntentUtils {

    fun openGoogleMaps(context: Context, latitude: Double, longitude: Double, label: String) {
        try {
            val encodedLabel = Uri.encode(label)
            val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($encodedLabel)")
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                // Fallback web url
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
            }
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
            } catch (err: Exception) {
                Toast.makeText(context, "Impossible d'ouvrir Google Maps", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openDirections(context: Context, latitude: Double, longitude: Double) {
        try {
            val navUri = Uri.parse("google.navigation:q=$latitude,$longitude")
            val navIntent = Intent(Intent.ACTION_VIEW, navUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (navIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(navIntent)
            } else {
                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
            }
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
            } catch (err: Exception) {
                Toast.makeText(context, "Impossible de lancer l'itinéraire", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun dialPhone(context: Context, phoneNumber: String) {
        try {
            val cleaned = phoneNumber.replace(" ", "").replace("-", "")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleaned"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible de composer le numéro", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(context: Context, email: String, facultyName: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "Demande d'information - $facultyName")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Aucune application de messagerie trouvée", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWebsite(context: Context, url: String) {
        try {
            val fullUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "http://$url"
            } else url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible d'ouvrir le lien : $url", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareFaculty(context: Context, faculty: Faculty) {
        try {
            val text = """
                🏛️ ${faculty.name}
                ${faculty.arabicName}
                
                🎓 Université : ${faculty.university}
                📍 Adresse : ${faculty.address} (${faculty.governorate})
                📞 Tél : ${faculty.phone}
                📧 Email : ${faculty.email}
                🌐 Site web : ${faculty.website}
                
                📍 Localisation Google Maps :
                https://www.google.com/maps/search/?api=1&query=${faculty.latitude},${faculty.longitude}
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, "Partager ${faculty.shortCode}"))
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur de partage", Toast.LENGTH_SHORT).show()
        }
    }
}
