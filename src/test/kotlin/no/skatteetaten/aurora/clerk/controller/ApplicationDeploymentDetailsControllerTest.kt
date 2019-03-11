package no.skatteetaten.aurora.clerk.controller

import no.skatteetaten.aurora.clerk.controller.security.BearerAuthenticationManager
import no.skatteetaten.aurora.clerk.service.PodService
import no.skatteetaten.aurora.clerk.service.openshift.token.UserDetailsProvider
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.test.context.support.WithUserDetails
import java.time.Instant

@WebMvcTest(
    ApplicationController::class,
    PodResourceAssembler::class,
    UserDetailsProvider::class,
    BearerAuthenticationManager::class
)
@AutoConfigureWebClient
@AutoConfigureRestDocs
class ApplicationDeploymentDetailsControllerTest : AbstractSecurityControllerTest() {

    @MockBean
    lateinit var podService: PodService

    private val name = "luke"
    private val started = Instant.now().toString()
    private val namespace = "jedi-test"

    private val luke = PodItem("$name-1", name, started, "Running")

    private val yoda = PodItem("yoda-1", "yoda", started, "Running")

    @Test
    @WithUserDetails
    fun `should get error if incorrect namespace in token`() {
        mockMvc.get(
            headers = HttpHeaders().authorization("Bearer <token>"),
            docsIdentifier = "error-pods",
            urlTemplate = UrlTemplate("/api/pods/{namespace}", "sith")
        ) {
            it.status(UNAUTHORIZED)
                .responseJsonPath("$.success").equalsValue(false)
                .responseJsonPath("$.message").equalsValue("Only an application in the same namespace can use clerk.")
        }
    }

    @Test
    @WithUserDetails
    fun `should get all pods in namespace`() {
        given(podService.getPodItems(namespace)).willReturn(listOf(luke, yoda))

        mockMvc.get(
            headers = HttpHeaders().authorization("Bearer <token>"),
            docsIdentifier = "list-pods",
            urlTemplate = UrlTemplate("/api/pods/{namespace}", namespace)
        ) {
            it.status(OK)
                .responseJsonPath("$.items[0]").equalsObject(luke)
                .responseJsonPath("$.items[1]").equalsObject(yoda)
        }
    }

    @Test
    @WithUserDetails
    fun `should get pods for application in namespace`() {
        given(podService.getPodItems(namespace, name)).willReturn(listOf(luke))

        mockMvc.get(
            headers = HttpHeaders().authorization("Bearer <token>"),
            docsIdentifier = "app-pods",
            urlTemplate = UrlTemplate("/api/pods/{namespace}?applicationName=$name", namespace)
        ) {
            it.status(OK).responseJsonPath("$.items[0]").equalsObject(luke)
        }
    }
}