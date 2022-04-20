package com.goldmedal.crm.util

import java.io.IOException
import java.net.ConnectException

class ApiException(message: String) : IOException(message)
class NoInternetException(message: String) : ConnectException(message)