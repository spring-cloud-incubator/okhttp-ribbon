/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.square.retrofit.webclient;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WebClientConverterFactory extends Converter.Factory {

	private static final Converter<ResponseBody, Object> EMPTY_CONVERTER = responseBody -> null;

	public WebClientConverterFactory() {
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		Class<?> rawType = getRawType(type);
		boolean isMono = rawType == Mono.class;
		if (rawType != Flux.class && !isMono) {

			if (rawType == Response.class) {
				Type publisherType = getParameterUpperBound(0, (ParameterizedType) type);
				Class<?> rawPublisherType = getRawType(publisherType);
				isMono = rawPublisherType == Mono.class;
				boolean isFlux = rawPublisherType == Flux.class;

				if (isMono || isFlux) {
					return EMPTY_CONVERTER;
				}
			}

			return null;
		}
		return EMPTY_CONVERTER;
	}

}
