package org.duckdns.pynetti.forecast

/*
FMIForecastXmlParser Parses FMI XML response to ForecastData data class for easier data management
 */


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.duckdns.pynetti.forecast.data.ForecastData
import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.StringReader


//data class ForecastData(var timeStamp:String, var name:String, var region:String, var country:String, var data :HashMap<String,LinkedHashMap<String,String>>)


class FMIForecastXmlParser(private val xmlFile: String? = null) {
    val foreCastData = ForecastData("", "", "", "", HashMap())

    constructor() : this(null) {

        if (xmlFile != null) {
            GlobalScope.launch { parse(xmlFile) }
        }
    }

    suspend fun parse(xmlFile: String): ForecastData {
        try {
            val stringInputSource = InputSource(StringReader(xmlFile))
            val builderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = builderFactory.newDocumentBuilder()
            val doc = docBuilder.parse(stringInputSource)
            foreCastData.timeStamp =
                doc.getElementsByTagName("wfs:FeatureCollection").item(0).attributes.item(0)
                    .nodeValue
            foreCastData.name = getNodeValue(doc.getElementsByTagName("gml:name").item(0))
            foreCastData.region = getNodeValue(doc.getElementsByTagName("target:region").item(0))
            foreCastData.country = getNodeValue(doc.getElementsByTagName("target:country").item(0))
            foreCastData.data = getNodeValues(doc)
            // MainActivity.locationPermissionManager.

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        }
        return foreCastData
    }

    // function to return node value
    suspend private fun getNodeValues(document: Document): HashMap<String, LinkedHashMap<String, String>> {
        val values = HashMap<String, LinkedHashMap<String, String>>()
        val nodeValuesList = document.getElementsByTagName("wml2:MeasurementTimeseries")
        for (i in 0 until nodeValuesList.length) {
            if (nodeValuesList.item(0).nodeType == Node.ELEMENT_NODE) {
                val element = nodeValuesList.item(i) as Element
                val nodeID = nodeValuesList.item(i).attributes.getNamedItem("gml:id")
                    .nodeValue.substringAfter("mts-1-1-", "")
                val nodeValues = LinkedHashMap<String, String>()
                val nodeList = element.getElementsByTagName("wml2:MeasurementTVP")
                var timeStamp = ""
                var value = ""
                for (j in 0 until nodeList.length) {
                    val childNodes = nodeList.item(j).childNodes
                    for (k in 0 until childNodes.length) {
                        val childNode = childNodes.item(k)
                        if (childNode.nodeType == 1.toShort()) {
                            when (childNode.nodeName) {
                                "wml2:time" -> timeStamp = childNode.childNodes.item(0).nodeValue
                                "wml2:value" -> value = childNode.childNodes.item(0).nodeValue
                            }
                            if (value == "NaN") {
                                value = "-"
                            }
                        }
                    }
                    nodeValues[timeStamp] = value
                }
                values[nodeID] = nodeValues
            }

        }
        return values

    }

    private fun getNodeValue(node: Node): String {
        val childNode = node.childNodes.item(0)
        return childNode.nodeValue
    }

}