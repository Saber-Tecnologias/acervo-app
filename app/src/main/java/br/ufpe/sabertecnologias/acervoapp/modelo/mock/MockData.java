package br.ufpe.sabertecnologias.acervoapp.modelo.mock;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.util.JsonParser;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

/**
 * Created by joaotrindade on 25/10/16.
 */

public class MockData {

    public static JSONObject meus_grupos, meus_itens;

    public static String MOCK_USER = "{" +
            "\'username\': \'jpttrindade\', " +
            "\'name\': \'Joao Paulo Tenorio\'," +
            "\'email\': \'example@email.com\'," +
            "\'token\': \'<example_token>\'" +
            "}";


    public static JSONObject getMeus_grupos(Context ctx) {

        if (meus_grupos == null) {
            meus_grupos = readMeusGruposFromJson(ctx);
        }

        return meus_grupos;
    }


    public static JSONObject getMeus_itens(Context ctx) {
        if (meus_itens == null) {
            meus_itens = readMeusItensFromJson(ctx);
        }

        return meus_itens;
    }

    private static JSONObject readMeusItensFromJson(Context ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            InputStreamReader fileReader = new InputStreamReader(ctx.getAssets().open("meus_itens_mock.json"));
            JsonReader reader = new JsonReader(fileReader);
            reader.beginObject();
            String name;
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("qt_itens")) {
                    jsonObject.put("qt_itens", reader.nextInt());
                } else if (name.equals("itens")) {
                    JSONArray jsonArray = new JSONArray();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        jsonArray.put(getItensObject(reader));
                    }
                    reader.endArray();
                    jsonObject.put("itens", jsonArray);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private static JSONObject getItensObject(JsonReader reader) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        reader.beginObject();
        String name;
        while (reader.hasNext()) {
            name = reader.nextName();
            DebugLog.d(name);
            if(name.equals("codigo")) {
                jsonObject.put("codigo", reader.nextInt());
            } else if (name.equals("situacao")) {
                if(reader.peek() != JsonToken.NULL) {
                    jsonObject.put("situacao", reader.nextString());
                } else {
                    jsonObject.put("favorito", null);
                    reader.nextNull();
                }
            } else if (name.equals("favorito")) {
                if(reader.peek() != JsonToken.NULL) {
                    jsonObject.put("favorito", reader.nextString());
                } else {
                    jsonObject.put("favorito", null);
                    reader.nextNull();
                }
            } else if (name.equals("avaliacao")) {
                if(reader.peek() != JsonToken.NULL) {
                    jsonObject.put("avaliacao", reader.nextString());
                } else {
                    jsonObject.put("avaliacao", null);
                    reader.nextNull();
                }
            } else if (name.equals("flag")) {
                if(reader.peek() != JsonToken.NULL) {
                    jsonObject.put("flag", reader.nextString());
                } else {
                    jsonObject.put("flag", null);
                    reader.nextNull();
                }
            } else if (name.equals("grupo_id")) {
                if(reader.peek() != JsonToken.NULL) {
                    jsonObject.put("grupo_id", reader.nextString());
                } else {
                    jsonObject.put("grupo_id", null);
                    reader.nextNull();
                }
            } else if (name.equals("grupo_nome")) {
                if(reader.peek() != JsonToken.NULL) {
                    jsonObject.put("grupo_nome", reader.nextString());
                } else {
                    jsonObject.put("grupo_nome", null);
                    reader.nextNull();
                }
            } else if (name.equals("detalhe")) {
                jsonObject.put("detalhe", JsonParser.readJsonObjectItemFromReader(reader));
            }
        }
        reader.endObject();
        return jsonObject;
    }


    public static ArrayList<Item> getItensFromDataServerDump(Context ctx) {

        try {
            InputStream inputStream = ctx.getAssets().open("response-export_dump.json");
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            JsonReader reader = new JsonReader(fileReader);
            return JsonParser.parseItem(reader).getItens();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static JSONObject getJsonDataServerDump(Context ctx) {

        try {
            InputStream inputStream = ctx.getAssets().open("response-export_dump.json");
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            JsonReader reader = new JsonReader(fileReader);
            return JsonParser.getJsonObjectFromReader(reader);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }


    private static JSONObject readMeusGruposFromJson(Context ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            InputStreamReader fileReader = new InputStreamReader(ctx.getAssets().open("meus_grupos_mock.json"));
            JsonReader reader = new JsonReader(fileReader);

            reader.beginObject();
            String name;
            while (reader.hasNext()) {
                name = reader.nextName();
                if(name.equals("qt_itens")) {
                    jsonObject.put("qt_itens", reader.nextInt());
                } else if (name.equals("grupos_itens")) {
                    if (reader.peek() != JsonToken.NULL) {
                        reader.beginArray();
                        JSONArray jsonArray = new JSONArray();
                        while (reader.hasNext()) {
                            jsonArray.put(getNextGrupoObject(reader));
                        }
                        jsonObject.put("grupos_itens", jsonArray);
                        reader.endArray();
                    } else {
                        reader.nextNull();
                    }
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static JSONObject getNextGrupoObject(JsonReader reader) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        reader.beginObject();
        String name;
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("id")) {
                jsonObject.put("id", reader.nextString());
            } else if (name.equals("nome")) {
                jsonObject.put("nome", reader.nextString());
            } else if (name.equals("cor")) {
                jsonObject.put("cor", reader.nextString());
            } else if (name.equals("codigo_itens")) {
                JSONArray jsonArray = new JSONArray();
                reader.beginArray();
                while (reader.hasNext()) {
                    jsonArray.put(reader.nextInt());
                }
                reader.endArray();
                jsonObject.put("codigos_itens", jsonArray);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return jsonObject;
    }


    public static JSONObject getSetup_servidor(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("versao", "1.0");
            jsonObject.put("horario_servidor", ""+System.currentTimeMillis());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("v1");
            jsonObject.put("protocolos_suportados", jsonArray);
            jsonObject.put("alertas", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
