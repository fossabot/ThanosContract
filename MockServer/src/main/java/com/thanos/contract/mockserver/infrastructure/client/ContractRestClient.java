package com.thanos.contract.mockserver.infrastructure.client;

import com.google.gson.reflect.TypeToken;
import com.thanos.contract.mockserver.exception.InfraException;
import com.thanos.contract.mockserver.infrastructure.client.dto.ContractDTO;
import com.thanos.contract.mockserver.infrastructure.client.dto.SchemaDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ContractRestClient {

    private static final String CONTRACT_SERVICE_BASE_URL = "http://localhost:8081/apis";
    OkHttpClient client = new OkHttpClient();

    public List<String> getAllContractIndex() throws IOException, InfraException {
        HttpUrl.Builder urlBuild = HttpUrl.parse(CONTRACT_SERVICE_BASE_URL + "/contracts/indexs").newBuilder();
        final String url = urlBuild.build().toString();
        log.debug("url going to approach is: {}", url);

        String result = httpGet(url);
        log.debug("HTTP GET result is {}", result);
        return JsonUtil.toObject(result, new TypeToken<List<String>>() {
        }.getType());

    }

    public List<ContractDTO> getContractByIndex(String indexName) throws IOException, InfraException {
        HttpUrl.Builder urlBuild = HttpUrl.parse(CONTRACT_SERVICE_BASE_URL + "/contracts").newBuilder();
        urlBuild.addQueryParameter("index", indexName);
        final String url = urlBuild.build().toString();
        log.debug("url going to approach is: {}", url);

        String result = httpGet(url);
        log.debug("HTTP GET result is {}", result);
        return JsonUtil.toObject(result, new TypeToken<List<ContractDTO>>() {
        }.getType());

    }

    public List<SchemaDTO> getSchemaByIndex(List<String> schemaNeededs) throws IOException, InfraException {
        HttpUrl.Builder urlBuild = HttpUrl.parse(CONTRACT_SERVICE_BASE_URL + "/schemas").newBuilder();
        schemaNeededs.forEach(schemaIndex -> {
            urlBuild.addQueryParameter("index", schemaIndex);
        });
        final String url = urlBuild.build().toString();
        log.debug("url going to approach is: {}", url);

        String result = httpGet(url);
        log.debug("HTTP GET result is {}", result);
        return JsonUtil.toObject(result, new TypeToken<List<SchemaDTO>>() {
        }.getType());
    }

    private String httpGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() < 300)
                return response.body().string();
            else {
                throw new InfraException("HTTP error with status " + response.code() + ":" + response.message());
            }
        }
    }
}
