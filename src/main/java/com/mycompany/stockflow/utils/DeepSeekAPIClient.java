/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.excepciones.AIAPIException;
import com.mycompany.stockflow.excepciones.ConfiguracionAIFaltanteException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Cliente para consumir la API de DeepSeek con configuración personalizada.
 * Permite enviar prompts, recibir respuestas y manejar excepciones propias del sistema.
 *
 * @author Stockflow Team
 * @version 1.0
 * @since 2025 
 **/

public class DeepSeekAPIClient {

    // Obtengo la configuración actual del sistema, que incluye API Key, modelo, temperatura, etc.
    private final ConfiguracionAI config;

    // En el constructor, inicializo la configuración usando el patrón Singleton.
    public DeepSeekAPIClient() {
        this.config = ConfiguracionAI.getInstance();
    }

    // Esta función me permite enviar un prompt a la API usando los parámetros por defecto definidos en la configuración.
    public String enviarPrompt(String prompt) throws AIAPIException, ConfiguracionAIFaltanteException {
        return enviarPrompt(prompt, config.getTemperature(), config.getMaxTokens());
    }

    // Aquí envío un prompt a la API con parámetros personalizados como temperatura y número máximo de tokens.
    public String enviarPrompt(String prompt, double temperature, int maxTokens)
            throws AIAPIException, ConfiguracionAIFaltanteException {

        try {
            // Abro la conexión HTTP con la URL configurada
            URL url = new URL(config.getApiUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(config.getTimeout());
            conn.setReadTimeout(config.getTimeout());

            // Construyo el cuerpo de la solicitud con el prompt y los parámetros
            JSONObject requestBody = construirRequestBody(prompt, temperature, maxTokens);

            // Envío el cuerpo de la solicitud a la API
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Verifico el código de respuesta HTTP
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Si la respuesta es exitosa, la leo y extraigo el contenido generado por la IA
                String response = leerRespuesta(conn);
                return extraerContenido(response);
            } else {
                // Si hay error, leo el mensaje de error y lanzo una excepción personalizada
                String errorResponse = leerError(conn);
                throw new AIAPIException(
                    "Error en API: " + errorResponse,
                    responseCode,
                    "HTTP_" + responseCode
                );
            }

        } catch (ConfiguracionAIFaltanteException e) {
            throw e;
        } catch (AIAPIException e) {
            throw e;
        } catch (Exception e) {
            // Capturo cualquier otro error de conexión y lo encapsulo en una excepción personalizada
            throw new AIAPIException("Error de conexión: " + e.getMessage(), e);
        }
    }

    // Esta función me permite probar si la conexión con la API está funcionando correctamente.
    public boolean probarConexion() {
        try {
            String respuesta = enviarPrompt("Responde solo OK", 0.1, 10);
            return respuesta != null && !respuesta.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Aquí construyo el cuerpo JSON que se enviará a la API, incluyendo el modelo, temperatura, tokens y el mensaje del usuario.
    private JSONObject construirRequestBody(String prompt, double temperature, int maxTokens) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", config.getModel());
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.put(message);

        requestBody.put("messages", messages);
        return requestBody;
    }

    // Esta función me permite leer la respuesta de la API cuando la solicitud fue exitosa.
    private String leerRespuesta(HttpURLConnection conn) throws Exception {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }
        br.close();
        return response.toString();
    }

    // Aquí leo el mensaje de error que devuelve la API cuando la solicitud falla.
    private String leerError(HttpURLConnection conn) throws Exception {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)
        );
        StringBuilder errorResponse = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            errorResponse.append(line.trim());
        }
        br.close();
        return errorResponse.toString();
    }

    // Esta función analiza el cuerpo de la respuesta JSON y extrae el contenido generado por la IA.
    private String extraerContenido(String responseBody) throws AIAPIException {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);

            if (jsonResponse.has("choices")) {
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject messageResponse = choice.getJSONObject("message");
                    return messageResponse.getString("content");
                }
            }

            throw new AIAPIException("Respuesta sin contenido válido");

        } catch (Exception e) {
            throw new AIAPIException("Error parseando respuesta: " + e.getMessage(), e);
        }
    }
}
