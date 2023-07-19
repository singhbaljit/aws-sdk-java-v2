package software.amazon.awssdk.http.auth.aws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.amazon.awssdk.http.auth.aws.AwsV4HttpSigner.CHUNKED_ENCODING;
import static software.amazon.awssdk.http.auth.aws.AwsV4HttpSigner.PAYLOAD_SIGNING;
import static software.amazon.awssdk.http.auth.aws.AwsV4HttpSigner.REGION_NAME;
import static software.amazon.awssdk.http.auth.aws.AwsV4HttpSigner.SERVICE_SIGNING_NAME;
import static software.amazon.awssdk.http.auth.aws.AwsV4HttpSigner.SIGNING_CLOCK;
import static software.amazon.awssdk.http.auth.aws.TestUtils.AnonymousCredentialsIdentity;
import static software.amazon.awssdk.http.auth.aws.TestUtils.TickingClock;
import static software.amazon.awssdk.http.auth.aws.TestUtils.generateBasicAsyncS3Request;
import static software.amazon.awssdk.http.auth.aws.TestUtils.generateBasicS3Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.auth.aws.chunkedencoding.AwsChunkedEncodingConfig;
import software.amazon.awssdk.http.auth.aws.internal.signer.AwsV4HeaderHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.BaseAwsV4HttpSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.DefaultAwsS3V4HttpSigner;
import software.amazon.awssdk.http.auth.spi.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.SyncSignRequest;
import software.amazon.awssdk.http.auth.spi.SyncSignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.utils.IoUtils;

public class AwsS3V4HttpSignerTest {

    private static final BaseAwsV4HttpSigner<?> signer = new DefaultAwsS3V4HttpSigner(
        new AwsV4HeaderHttpSigner(
            BaseAwsV4HttpSigner.create()
        ),
        // override the default encoding config to match the example given here:
        // https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-streaming.html
        AwsChunkedEncodingConfig.builder()
            .chunkSize(64 * 1024)
            .build()
    );

    @Test
    public void sign_withBasicRequest_shouldSign() {
        SyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicS3Request(
            AwsCredentialsIdentity.create("akid", "skid"),
            (httpRequest -> {
            }),
            (signRequest -> {
            })
        );

        SyncSignedRequest signedRequest = signer.sign(request);

        assertThat(signedRequest.request().firstMatchingHeader("Host")).hasValue(request.request().host());
        assertThat(signedRequest.request().firstMatchingHeader("X-Amz-Date")).hasValue("19700101T000000Z");
        assertThat(signedRequest.request().firstMatchingHeader("x-amz-content-sha256")).hasValue("UNSIGNED-PAYLOAD");
        assertThat(signedRequest.request().firstMatchingHeader("Authorization"))
            .hasValue("AWS4-HMAC-SHA256 Credential=akid/19700101/us-west-2/s3/aws4_request, "
                + "SignedHeaders=host;x-amz-content-sha256;x-amz-date, "
                + "Signature=a3b97f9de337ab254f3b366c3d0b3c67016d2d8d8ba7e0e4ddab0ccebe84992a");
    }

    @Test
    public void sign_withEncodedCharacters_shouldNotThrow() {
        URI target = URI.create("https://test.com/%20foo");

        SyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicS3Request(
            AwsCredentialsIdentity.create("akid", "skid"),
            (httpRequest -> {
                httpRequest.uri(target);
                httpRequest.encodedPath(target.getPath());
            }),
            (signRequest -> {
            })
        );

        assertDoesNotThrow(() -> signer.sign(request));
    }

    @Test
    public void sign_withSignedPayloadAndChunkedEncoding_shouldSignRequestAndPayload() throws IOException {
        URI objectPath = URI.create("https://s3.us-west-2.amazonaws.com/examplebucket/chunkObject.txt");
        byte[] data = new byte[66560];
        Arrays.fill(data, (byte) 'a');

        String expectedEncodedPayload =
            "10000;chunk-signature=ad80c730a21e5b8d04586a2213dd63b9a0e99e0e2307b0ade35a65485a288648\r\n" +
                new String(Arrays.copyOfRange(data, 0, 65536), StandardCharsets.UTF_8) +
                "\r\n400;chunk-signature=0055627c9e194cb4542bae2aa5492e3c1575bbb81b612b7d234b86a503ef5497\r\n" +
                new String(Arrays.copyOfRange(data, 65536, 66560), StandardCharsets.UTF_8) +
                "\r\n0;chunk-signature=b6c6ea8a5354eaf15b3cb7646744f4275b71ea724fed81ceb9323e279d449df9\r\n\r\n";

        SyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicS3Request(
            AwsCredentialsIdentity.create("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"),
            (httpRequest -> {
                httpRequest.method(SdkHttpMethod.PUT);
                httpRequest.uri(objectPath);
                httpRequest.host("s3.amazonaws.com");
                httpRequest.encodedPath(objectPath.getPath());
                httpRequest.putHeader("x-amz-storage-class", "REDUCED_REDUNDANCY");

            }),
            (signRequest -> {
                signRequest.payload(() -> new ByteArrayInputStream(data));
                signRequest.putProperty(REGION_NAME, "us-east-1");
                signRequest.putProperty(SIGNING_CLOCK, new TickingClock(Instant.parse(
                    "2013-05-24T00:00:00Z")));
                signRequest.putProperty(CHUNKED_ENCODING, true);
                signRequest.putProperty(PAYLOAD_SIGNING, true);

            })
        );

        SyncSignedRequest signedRequest = signer.sign(request);

        assertThat(signedRequest.request().firstMatchingHeader("Host")).hasValue(request.request().host());
        assertThat(signedRequest.request().firstMatchingHeader("X-Amz-Date")).hasValue("20130524T000000Z");
        assertThat(signedRequest.request().firstMatchingHeader("Content-Encoding")).hasValue("aws-chunked");
        assertThat(signedRequest.request().firstMatchingHeader("Content-Length")).hasValue("66824");
        assertThat(signedRequest.request().firstMatchingHeader("Authorization"))
            .hasValue("AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20130524/us-east-1/s3/aws4_request, "
                + "SignedHeaders=content-encoding;content-length;host;x-amz-content-sha256;x-amz-date;" +
                "x-amz-decoded-content-length;x-amz-storage-class, "
                + "Signature=4f232c4386841ef735655705268965c44a0e4690baa4adea153f7db9fa80a0a9");

        String encodedPayload = IoUtils.toUtf8String(signedRequest.payload().get().newStream());

        assertThat(encodedPayload).isEqualTo(expectedEncodedPayload);
    }

    @Test
    public void sign_withoutRegionNameProperty_throws() {
        SyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicS3Request(
            AwsCredentialsIdentity.create("access", "secret"),
            (httpRequest -> {
            }),
            (signRequest -> signRequest.putProperty(REGION_NAME, null))
        );

        NullPointerException exception = assertThrows(NullPointerException.class, () -> signer.sign(request));

        assertThat(exception.getMessage()).contains("must not be null");
    }

    @Test
    public void sign_withoutServiceSigningNameProperty_throws() {
        SyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicS3Request(
            AwsCredentialsIdentity.create("access", "secret"),
            (httpRequest -> {
            }),
            (signRequest -> signRequest.putProperty(SERVICE_SIGNING_NAME, null))
        );

        NullPointerException exception = assertThrows(NullPointerException.class, () -> signer.sign(request));

        assertThat(exception.getMessage()).contains("must not be null");
    }

    @Test
    public void sign_withSessionCredentials_shouldSignAndAddTokenHeader() {
        SyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicS3Request(
            AwsSessionCredentialsIdentity.create("akid", "skid", "tok"),
            (httpRequest -> {
            }),
            (signRequest -> {
            })
        );

        SyncSignedRequest signedRequest = signer.sign(request);

        assertThat(signedRequest.request().firstMatchingHeader("Authorization"))
            .hasValue("AWS4-HMAC-SHA256 Credential=akid/19700101/us-west-2/s3/aws4_request, "
                + "SignedHeaders=host;x-amz-content-sha256;x-amz-date;x-amz-security-token, "
                + "Signature=8ddac1b6c134f24b66a43fa1e652a0f25174be77a99bbbdd1ec01ca3f56e15ad");
    }

    @Test
    public void sign_withAnonymousCredentials_shouldNotSign() {
        SyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicS3Request(
            new AnonymousCredentialsIdentity(),
            (httpRequest -> {
            }),
            (signRequest -> {
            })
        );

        SyncSignedRequest signedRequest = signer.sign(request);

        assertNull(signedRequest.request().headers().get("Authorization"));
    }

    @Test
    public void signAsync_throwsUnsupportedOperationException() {
        AsyncSignRequest<? extends AwsCredentialsIdentity> request = generateBasicAsyncS3Request(
            AwsCredentialsIdentity.create("access", "secret"),
            (httpRequest -> {
            }),
            (signRequest -> {
            })
        );

        assertThrows(UnsupportedOperationException.class, () -> signer.signAsync(request));
    }

}
