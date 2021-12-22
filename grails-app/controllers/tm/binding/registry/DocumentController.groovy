package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import grails.web.mapping.LinkGenerator

import javax.servlet.ServletException

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN"])
class DocumentController {

    def springSecurityService

    DocumentService documentService

    LinkGenerator grailsLinkGenerator

    def index() { }

    def administer() {

        // redirect to public view if org admin role
        if (springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAllGranted("ROLE_ORG_ADMIN")) {
            return redirect(controller:'publicApi', action:'documents')
        }
    }

    def add()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

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
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Document document = documentService.get(params.id)

        withFormat  {
            json {
                render document as JSON
            }
        }
    }

    def delete()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Document document = documentService.delete(params.ids)

        withFormat  {
            json {
                render document as JSON
            }
        }
    }

    def update()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

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

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def list()  {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAllGranted("ROLE_ADMIN"))

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
        log.info("Viewing PDF document: ${params.id}...")

        // get the document associated to the binary id
        BinaryObject binaryObject = BinaryObject.findById(params.id);
        if( !binaryObject ) {
            throw new ServletException("No such binary object: ${params.id}")
        }

        Document doc = Document.findByBinaryObject(binaryObject);
        if( !doc ) {
            throw new ServletException("No such document asscociated to binary object id: ${params.id}")
        }

        boolean  isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")
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
