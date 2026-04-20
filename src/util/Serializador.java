package util;

import dto.FormatoSalida;
import dto.RespuestaPadron;
import entidades.Direccion;
import entidades.Persona;

public final class Serializador {

    private Serializador() {
    }

    public static String serializar(RespuestaPadron respuesta, FormatoSalida formatoSalida) {
        return formatoSalida == FormatoSalida.XML ? toXml(respuesta) : toJson(respuesta);
    }

    public static String toJson(RespuestaPadron respuesta) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"exito\": ").append(respuesta.isExito()).append(",\n");
        sb.append("  \"codigo\": ").append(respuesta.getCodigoHttp()).append(",\n");
        sb.append("  \"mensaje\": \"").append(escapeJson(respuesta.getMensaje())).append("\"");

        if (respuesta.isExito()) {
            Persona persona = respuesta.getPersona();
            Direccion direccion = respuesta.getDireccion();
            sb.append(",\n  \"persona\": {")
              .append("\n    \"cedula\": \"").append(escapeJson(persona.getCedula())).append("\",")
              .append("\n    \"codigoElectoral\": \"").append(escapeJson(persona.getCodigoElectoral())).append("\",")
              .append("\n    \"nombre\": \"").append(escapeJson(persona.getNombre())).append("\",")
              .append("\n    \"primerApellido\": \"").append(escapeJson(persona.getPrimerApellido())).append("\",")
              .append("\n    \"segundoApellido\": \"").append(escapeJson(persona.getSegundoApellido())).append("\",")
              .append("\n    \"nombreCompleto\": \"").append(escapeJson(persona.getNombreCompleto())).append("\"")
              .append("\n  },\n");

            if (direccion != null) {
                sb.append("  \"direccion\": {")
                  .append("\n    \"codigoElectoral\": \"").append(escapeJson(direccion.getCodigoElectoral())).append("\",")
                  .append("\n    \"provincia\": \"").append(escapeJson(direccion.getProvincia())).append("\",")
                  .append("\n    \"canton\": \"").append(escapeJson(direccion.getCanton())).append("\",")
                  .append("\n    \"distrito\": \"").append(escapeJson(direccion.getDistrito())).append("\"")
                  .append("\n  }\n");
            } else {
                sb.append("  \"direccion\": null\n");
            }
        } else {
            sb.append("\n");
        }

        sb.append("}");
        return sb.toString();
    }

    public static String toXml(RespuestaPadron respuesta) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<respuesta>\n");
        sb.append("  <exito>").append(respuesta.isExito()).append("</exito>\n");
        sb.append("  <codigo>").append(respuesta.getCodigoHttp()).append("</codigo>\n");
        sb.append("  <mensaje>").append(escapeXml(respuesta.getMensaje())).append("</mensaje>\n");

        if (respuesta.isExito()) {
            Persona persona = respuesta.getPersona();
            Direccion direccion = respuesta.getDireccion();

            sb.append("  <persona>\n");
            sb.append("    <cedula>").append(escapeXml(persona.getCedula())).append("</cedula>\n");
            sb.append("    <codigoElectoral>").append(escapeXml(persona.getCodigoElectoral())).append("</codigoElectoral>\n");
            sb.append("    <nombre>").append(escapeXml(persona.getNombre())).append("</nombre>\n");
            sb.append("    <primerApellido>").append(escapeXml(persona.getPrimerApellido())).append("</primerApellido>\n");
            sb.append("    <segundoApellido>").append(escapeXml(persona.getSegundoApellido())).append("</segundoApellido>\n");
            sb.append("    <nombreCompleto>").append(escapeXml(persona.getNombreCompleto())).append("</nombreCompleto>\n");
            sb.append("  </persona>\n");

            if (direccion != null) {
                sb.append("  <direccion>\n");
                sb.append("    <codigoElectoral>").append(escapeXml(direccion.getCodigoElectoral())).append("</codigoElectoral>\n");
                sb.append("    <provincia>").append(escapeXml(direccion.getProvincia())).append("</provincia>\n");
                sb.append("    <canton>").append(escapeXml(direccion.getCanton())).append("</canton>\n");
                sb.append("    <distrito>").append(escapeXml(direccion.getDistrito())).append("</distrito>\n");
                sb.append("  </direccion>\n");
            } else {
                sb.append("  <direccion xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n");
            }
        }

        sb.append("</respuesta>");
        return sb.toString();
    }

    private static String escapeJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String escapeXml(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
