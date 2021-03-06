package com.thanos.contract.mockserver.infrastructure.client;

import com.google.gson.reflect.TypeToken;
import com.thanos.contract.mockserver.exception.InfraException;
import com.thanos.contract.mockserver.infrastructure.dto.ContractDTO;
import com.thanos.contract.mockserver.infrastructure.dto.SchemaDTO;
import com.thanos.contract.mockserver.infrastructure.parser.PropertiesParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ContractRestClient {

    private OkHttpClient client;
    private String basePath;

    public ContractRestClient() {
        this.basePath = PropertiesParser.getContractServiceBaseHttpPath();
        this.client = new OkHttpClient();
    }

    public List<String> getAllContractIndex() throws IOException, InfraException {
        HttpUrl.Builder urlBuild = HttpUrl.parse(basePath + "/contracts/indexs").newBuilder();
        final String url = urlBuild.build().toString();
        log.debug("url going to approach is: {}", url);

        String result = httpGet(url);
        log.debug("HTTP GET result is {}", result);
        return JsonUtil.toObject(result, new TypeToken<List<String>>() {
        }.getType());

    }

    public List<ContractDTO> getContractByIndex(String indexName) throws IOException, InfraException {
        HttpUrl.Builder urlBuild = HttpUrl.parse(basePath + "/contracts").newBuilder();
        urlBuild.addQueryParameter("index", indexName);
        final String url = urlBuild.build().toString();
        log.debug("url going to approach is: {}", url);

        String result = httpGet(url);
        log.debug("HTTP GET result is {}", result);
        return JsonUtil.toObject(result, new TypeToken<List<ContractDTO>>() {
        }.getType());

    }

    public List<SchemaDTO> getSchemaByIndex(List<String> schemaNeededs) throws IOException, InfraException {
        HttpUrl.Builder urlBuild = HttpUrl.parse(basePath + "/schemas").newBuilder();
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
