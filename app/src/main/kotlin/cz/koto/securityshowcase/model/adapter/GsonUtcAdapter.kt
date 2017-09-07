package cz.koto.securityshowcase.model.adapter

import com.google.gson.*
import cz.koto.securityshowcase.SecurityConfig
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GsonUtcDateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

	private val dateFormat: DateFormat


	init {
		dateFormat = SimpleDateFormat(SecurityConfig.getApiDateFormatUtc())
		//This is the key line which converts the date to UTC which cannot be accessed with the default serializer
		dateFormat.timeZone = TimeZone.getTimeZone("UTC")
	}


	@Synchronized override fun serialize(date: Date, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
		return JsonPrimitive(convertDate(date))
	}


	@Synchronized override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): Date {
		try {
			return dateFormat.parse(jsonElement.asString)
		} catch (e: ParseException) {
			throw JsonParseException(e)
		}

	}


	fun convertDate(date: Date): String {
		return dateFormat.format(date)
	}
}