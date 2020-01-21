package hu.selester.selnet.objects

class SessionClass() {
    companion object {
        private val dataMap = mutableMapOf<String, String>()

        fun getValue(key: String): String? {
            return dataMap[key]
        }

        fun setValue(key: String, value: String) {
            dataMap[key] = value
        }

    }

}