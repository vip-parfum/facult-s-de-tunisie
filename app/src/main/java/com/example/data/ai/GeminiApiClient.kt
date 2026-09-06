package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.TaskItem
import com.example.data.model.UserCognitiveProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getTunisiaOrientationAdvice(
        bacBranch: String,
        score: String,
        targetInterests: String,
        preferredGovernorate: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackOrientationAdvice(bacBranch, score, targetInterests, preferredGovernorate)
        }

        try {
            val prompt = """
                Tu es un expert national de l'orientation universitaire en Tunisie (Tawjih Jami3i - التوجيه الجامعي).
                Un futur étudiant ou bachelier tunisien sollicite tes conseils personnalisés :
                - Branche du Bac : $bacBranch
                - Score estimé / Formule Globale (FG) : $score
                - Centres d'intérêt & Ambitions : $targetInterests
                - Région / Gouvernorat privilégié : $preferredGovernorate

                Rédige en français avec quelques termes en arabe tunisien (si pertinent) un guide d'orientation clair, structuré et bienveillant :
                1. 🎓 Facultés et Écoles Recommandées en Tunisie (nom précis des facultés comme FMT, FST, INSAT, ENIT, IHEC, FSEG, FSJPST, etc.)
                2. 📈 Analyse des chances d'admission selon le score et la section
                3. 💼 Débouchés professionnels réels sur le marché de l'emploi en Tunisie et à l'international
                4. 💡 Conseils pratiques pour réussir sa première année universitaire
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url("$BASE_URL$MODEL:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) return@withContext text
                    }
                }
            }
            generateFallbackOrientationAdvice(bacBranch, score, targetInterests, preferredGovernorate)
        } catch (e: Exception) {
            Log.e(TAG, "Orientation AI error", e)
            generateFallbackOrientationAdvice(bacBranch, score, targetInterests, preferredGovernorate)
        }
    }

    private fun generateFallbackOrientationAdvice(
        bacBranch: String,
        score: String,
        targetInterests: String,
        preferredGovernorate: String
    ): String {
        return buildString {
            append("### 🇹🇳 Guide d'Orientation Universitaire Tunisie\n\n")
            append("**Profil bachelier :** Section **$bacBranch** • Score indicatif : **$score**\n")
            append("**Centres d'intérêt :** $targetInterests • **Région souhaitée :** $preferredGovernorate\n\n")
            
            append("### 🏛️ Facultés et Établissements Recommandés\n")
            when {
                targetInterests.contains("médecine", ignoreCase = true) || targetInterests.contains("santé", ignoreCase = true) -> {
                    append("- **Faculté de Médecine de Tunis (FMT)** ou **Faculté de Médecine de Sousse (FMS)** : Formation médicale d'excellence avec stages cliniques au CHU.\n")
                    append("- **Faculté de Pharmacie de Monastir (FPM)** ou **Médecine Dentaire de Monastir (FMDM)** : Les deux uniques facultés nationales dans ces spécialités.\n")
                    append("- **ISTMT Tunis** : Pour allier technologie, instrumentation et sciences médicales.\n\n")
                }
                targetInterests.contains("ingénieur", ignoreCase = true) || targetInterests.contains("informatique", ignoreCase = true) || targetInterests.contains("tech", ignoreCase = true) -> {
                    append("- **INSAT Tunis** (Centre Urbain Nord) : Cursus intégré d'ingénieur en Génie Logiciel ou Réseaux & Télécoms sans concours prépa.\n")
                    append("- **Instituts Préparatoires (IPEIT, IPEIEM, IPEIM, IPEIS)** puis **ENIT Tunis, ENSI Manouba, ENIS Sfax, ENISo Sousse** : La voie royale des grandes écoles d'ingénieurs.\n")
                    append("- **Faculté des Sciences (FST Tunis, FSM Monastir, FSS Sfax)** : Licences et Mastères en Informatique et Intelligence Artificielle.\n\n")
                }
                targetInterests.contains("droit", ignoreCase = true) || targetInterests.contains("politique", ignoreCase = true) -> {
                    append("- **Faculté des Sciences Juridiques, Politiques et Sociales de Tunis (FSJPST)** : Référence en droit international et constitutionnel.\n")
                    append("- **Faculté de Droit et des Sciences Politiques de Tunis (FDSPT)** au Campus El Manar : Carrières judiciaires, barreau et magistrature.\n")
                    append("- **Faculté de Droit de Sfax** & **FDSP Sousse** pour le Sahel et le Sud.\n\n")
                }
                else -> {
                    append("- **IHEC Carthage** ou **ISG Tunis** : Formations renommées en finance, marketing et business analytics.\n")
                    append("- **FSEGT Tunis** ou **FSEG Sfax** : Référence pour l'expertise comptable, l'économie et la gestion des entreprises.\n")
                    append("- **FLAHM Manouba** & **FSHST 9 Avril** : Pour les langues étrangères, la traduction, la psychologie et la sociologie.\n\n")
                }
            }

            append("### 📌 Conseils Stratégiques pour le Choix du Tour d'Orientation\n")
            append("- **Calcul de la formule (FG) :** Comparez votre score avec les scores du dernier admis des 3 dernières sessions du guide du Ministère de l'Enseignement Supérieur (MesRS).\n")
            append("- **Bonification géographique :** N'oubliez pas les 7% de bonification pour les filières demandées dans votre région d'origine.\n")
            append("- **Logement et transport :** Prenez en compte la proximité des foyers universitaires et des lignes de métro/bus (Campus Manar, Zarrouk, Zrig, Erriadh).")
        }
    }

    suspend fun getCognitiveCoachingAdvice(
        profile: UserCognitiveProfile,
        tasks: List<TaskItem>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackCognitiveAdvice(profile, tasks)
        }

        try {
            val tasksSummary = tasks.joinToString("\n") {
                "- ${it.getFormattedStartTime()} : ${it.title} (Faculté: ${it.faculty}, Charge cognitive: ${it.cognitiveLoad}/5, Durée: ${it.durationMinutes}m)"
            }

            val prompt = """
                Tu es un neuro-ergonome et expert en optimisation cognitive du temps.
                Voici le profil intellectuel de l'utilisateur :
                - Chronotype : ${profile.chronotype}
                - Capacité d'attention continue optimale : ${profile.attentionSpanMinutes} minutes
                - Score Logique & Analyse : ${profile.logicAnalyticalScore}/100
                - Score Créativité : ${profile.creativeLateralScore}/100
                - Score Mémorisation : ${profile.memoryLearningScore}/100
                - Score Attention Soutenue : ${profile.sustainedAttentionScore}/100
                - Score Endurance Mentale : ${profile.mentalEnduranceScore}/100

                Voici ses tâches planifiées pour aujourd'hui :
                $tasksSummary

                Rédige en français un diagnostic concis, encourageant et percutant (en 3 parties claires avec puces) :
                1. 🧠 Diagnostic de charge mentale : alignement des facultés et risques de surmenage
                2. ⚡ Recommandation neuro-ergonomique personnalisée pour aujourd'hui
                3. 🌿 Protocole de récupération cognitive (pauses optimales selon son attention span de ${profile.attentionSpanMinutes}m)
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url("$BASE_URL$MODEL:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) return@withContext text
                    }
                }
            }
            Log.w(TAG, "Gemini call response: ${response.code} $responseBody")
            generateFallbackCognitiveAdvice(profile, tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini call failed", e)
            generateFallbackCognitiveAdvice(profile, tasks)
        }
    }

    private fun generateFallbackCognitiveAdvice(
        profile: UserCognitiveProfile,
        tasks: List<TaskItem>
    ): String {
        val chronotypeName = when (profile.chronotype) {
            "NIGHT_OWL" -> "Hibou Nocturne"
            "INTERMEDIATE" -> "Rythme Équilibré"
            else -> "Alouette Matinale"
        }

        val peakHours = when (profile.chronotype) {
            "NIGHT_OWL" -> "17h30 à 21h30"
            "INTERMEDIATE" -> "09h30 à 12h30 et 16h00 à 18h30"
            else -> "08h30 à 11h30"
        }

        val totalLoad = tasks.sumOf { it.cognitiveLoad }
        val highLoadCount = tasks.count { it.cognitiveLoad >= 4 }

        return buildString {
            append("### 🧠 Diagnostic Neuro-Ergonomique\n\n")
            append("**Profil identifié :** $chronotypeName • **Fenêtre d'efficience maximale :** $peakHours.\n")
            append("Votre capacité de raisonnement analytique est évaluée à **${profile.logicAnalyticalScore}/100** avec un empan d'attention continue de **${profile.attentionSpanMinutes} minutes**.\n\n")
            
            if (highLoadCount >= 2) {
                append("⚠️ **Alerte charge cognitive :** Vous avez $highLoadCount tâches à forte intensité intellectuelle. Veillez à ne pas les enchaîner sans sas de décompression.\n\n")
            } else {
                append("✅ **Équilibre mental optimal :** Votre répartition d'effort intellectuel est saine et préserve vos neurotransmetteurs d'attention.\n\n")
            }

            append("### ⚡ Recommandations Stratégiques\n")
            append("- **Sanctuaire de concentration :** Réservez la plage de $peakHours pour vos travaux de logique et d'analyse pure (charge 4-5).\n")
            append("- **Zone créative & diffuse :** Accordez-vous des plages d'idéation libre lorsque le cortex préfrontal relâche la pression.\n")
            append("- **Décharge mentale :** Évacuez les e-mails et la logistique (charge 1-2) pendant le creux de début d'après-midi.\n\n")

            append("### 🌿 Protocole de Récupération (${profile.attentionSpanMinutes}m / 10m)\n")
            append("Après chaque cycle de **${profile.attentionSpanMinutes} minutes**, pratiquez 5 à 10 minutes de repos sans écran (marche, hydratation, regard au loin) pour réinitialiser votre vigilance.")
        }
    }
}
