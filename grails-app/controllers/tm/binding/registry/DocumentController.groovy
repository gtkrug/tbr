package tm.binding.registry

import grails.converters.JSON
import grails.web.mapping.LinkGenerator
import org.gtri.fj.data.Option
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

@PreAuthorize('hasAnyAuthority("tbr-admin", "tbr-org-admin")')
class DocumentController {

    DocumentService documentService

    LinkGenerator grailsLinkGenerator

    def index() { }

    def administer() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        // redirect to public view if org admin role
        if (userOption.isSome() && SecurityContextHolder.getContext().getAuthentication().authenticated && userOption.some().isOrgAdmin()) {
            return redirect(controller:'publicApi', action:'documents')
        }
    }

    def add()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Document document = documentService.add(params.filename
                , params.description
                , params.binaryId
                , params.publicDocument
        )


        withFormat  {
            json {
                render document as JSON
            }
        }
    }

    def get()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Document document = documentService.get(params.id)

        withFormat  {
            json {
                render document as JSON
            }
        }
    }

    def delete()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Document document = documentService.delete(params.ids)

        withFormat  {
            json {
                render document as JSON
            }
        }
    }

    def update()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Document document = documentService.update(params.id
                , params.filename
                , params.description
                , params.binaryId
                , params.publicDocument)

        withFormat  {
            json {
                render document as JSON
            }
        }
    }

    @PreAuthorize('permitAll()')
    def list()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        Map results = [:]
        results.put("editable", userOption.isSome() &&
                SecurityContextHolder.getContext().getAuthentication().authenticated &&
                userOption.some().isAdmin())

        // get binary url
        def binaryUrl = grailsLinkGenerator.link(controller: 'binary', action: 'upload')

        results.put("binaryUrl", binaryUrl)

        def documents = documentService.list()

        results.put("records", documents)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }

    def pdf() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        log.info("Viewing PDF document: ${params.id}...")

        // get the document associated to the binary id
        BinaryObject binaryObject = BinaryObject.findById(params.id);
        if( !binaryObject ) {
            log.error("No such binary object: ${params.id}!")
            return redirect(controller:'error', action:'notFound404')
        }

        Document doc = Document.findByBinaryObject(binaryObject);
        if (!doc) {
            log.error("No such document associated to binary object id: ${params.id}!")
            return redirect(controller:'error', action:'notFound404')
        }

        boolean  isAdmin = userOption.isSome() && userOption.some().isAdmin()
        boolean isPublic = doc.publicDocument

        if (isAdmin || isPublic) {
            response.setHeader("Content-length", binaryObject.fileSize.toString())
            response.setHeader("Content-Disposition",
                    "inline; filename= ${URLEncoder.encode(binaryObject.originalFilename ?: "", "UTF-8")}")
            String mimeType = binaryObject.mimeType;
            if (mimeType == "text/xhtml") {
                mimeType = "text/html"; // A hack around XHTML display in browsers.
            }
            response.setContentType(mimeType);
            File outputFile = binaryObject.content.toFile();
            FileInputStream fileInputStream = new FileInputStream(outputFile);

            return render(file: fileInputStream, contentType: mimeType);
        } else {
            return redirect(controller:'error', action:'notAuthorized401')
        }
    }
}
