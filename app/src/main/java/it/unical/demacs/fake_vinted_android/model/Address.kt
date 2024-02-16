package it.unical.demacs.fake_vinted_android.model

data class Address (
 val username : String,
 val via: String,
 val numerocivico: String,
 val cap: String,
 val citta: String,
 val provincia: String
) {


 companion object {
  fun validateStreet(street: String): Boolean {
   if (street.isNotEmpty()) {
    return true
   }
   return false
  }

  fun validateStreetNumber(streetNumber: String): Boolean {
   try {
     if (streetNumber.isNotEmpty() && streetNumber.toInt() != 0) {
     return true
    }
    return false
   } catch (e: Exception) {
    return false
   }
  }

  fun validateCity(city: String): Boolean {
   if (city.isNotEmpty()) {
    return true
   }
   return false
  }

  fun validateProvince(province: String): Boolean {
   if (province.isNotEmpty() && province.length == 2) {
    return true
   }
   return false
  }

  fun validateZipCode(zipCode: String): Boolean {
   try {
    if (zipCode.isNotEmpty() && zipCode.toInt() != 0) {
     return true
    }
    return false
   } catch (e: Exception) {
    return false
   }
  }

    fun validateCountry(country: String): Boolean {
      if (country.isNotEmpty()) {
       return true
    }
      return false
   }
 }
}


