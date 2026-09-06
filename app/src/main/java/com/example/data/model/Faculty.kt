package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faculties")
data class Faculty(
    @PrimaryKey val id: String,
    val name: String,                    // e.g. "Faculté de Médecine de Tunis"
    val arabicName: String,              // e.g. "كلية الطب بتونس"
    val shortCode: String,               // e.g. "FMT"
    val university: String,              // e.g. "Université de Tunis El Manar"
    val universityCode: String,          // e.g. "UTM"
    val governorate: String,             // e.g. "Tunis", "Monastir", "Sfax"
    val region: String,                  // "Grand Tunis", "Sahel", "Nord", "Centre", "Sud"
    val address: String,                 // e.g. "15 Rue Djebel Lakhdar, La Rabta, 1007 Tunis"
    val latitude: Double,                // 36.8028
    val longitude: Double,               // 10.1558
    val phone: String,                   // "+216 71 563 560"
    val email: String,                   // "fmt@fmt.utm.tn"
    val website: String,                 // "http://www.fmt.rnu.tn"
    val category: String,                // "Médecine & Santé", "Sciences & Technologies", "Droit & Politique", "Économie & Gestion", "Lettres & Humaines", "Ingénierie & Informatique"
    val description: String,
    val degrees: String,                 // Semi-colon separated list of diplomas/formations
    val studentCount: String,            // e.g. "~4 200 étudiants"
    val foundationYear: Int,             // e.g. 1964
    val isFavorite: Boolean = false,
    val notes: String = ""
) {
    val degreesList: List<String>
        get() = degrees.split(";").map { it.trim() }.filter { it.isNotEmpty() }
}
