package sn.uam.polytech.misid.web.rest;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.uam.polytech.misid.repository.search.UserSearchRepository;
import sn.uam.polytech.misid.service.UserService;
import sn.uam.polytech.misid.service.dto.UserDTO;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserService userService;
    private final UserSearchRepository userSearchRepository;

    public PublicUserResource(UserSearchRepository userSearchRepository, UserService userService) {
        this.userService = userService;
        this.userSearchRepository = userSearchRepository;
    }

    /**
     * {@code GET /users} : get all users with only public information - calling this method is allowed for anyone.
     *
     * @param request a {@link ServerHttpRequest} request.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public Mono<ResponseEntity<Flux<UserDTO>>> getAllPublicUsers(
        ServerHttpRequest request,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all public User names");

        return userService
            .countManagedUsers()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(userService.getAllPublicUsers(pageable)));
    }

    /**
     * {@code SEARCH /users/_search/:query} : search for the User corresponding to the query.
     *
     * @param query the query to search.
     * @return the result of the search.
     */
    @GetMapping("/users/_search/{query}")
    public Mono<List<UserDTO>> search(@PathVariable("query") String query) {
        return userSearchRepository.search(query).map(UserDTO::new).collectList();
    }
}
