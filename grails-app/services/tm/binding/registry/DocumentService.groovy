package tm.binding.registry

import grails.gorm.transactions.Transactional
import tm.binding.registry.util.TBRProperties

@Transactional
class DocumentService {

    def serviceMethod() {

    }
    def add(String... args) {
        log.info("add -> ${args[0]}")

        BinaryObject binaryObject = BinaryObject.findById(args[2])

        Document document = new Document(
                filename: args[0]
                , url: getDocumentUrl(binaryObject.id) // TODO: add binary object id
                , publicUrl: getPublicDocumentUrl(args[0])
                , description: args[1] // TODO: add binary object id
                , binaryObject: binaryObject
                , publicDocument: args[3]
        )

        document.save(true)
        return document
    }

    /**
     * attempt to find by id first, then email
     * @param args
     * @return
     */
    def get(String... args) {
        log.info("get -> ${args[0]}")

        Document document = null

        try  {
            document = Document.get(Integer.parseInt(args[0]))
        } catch (NumberFormatException nfe) {
            log.error("Invalid Document Id!")
        }
        return document
    }

    /**
     * get a document, used to check for existing documents
     * @param document
     * @return
     */
    def get(Document document) {
        log.info("get -> ${document}")

        return Document.find(document)
    }

    /**
     * update the document attributes
     * @param args
     * @return
     */
    def update(String... args) {
        log.info("update -> ${args[0]}")

        BinaryObject binaryObject = BinaryObject.findById(args[3])

        Document document = null
        try  {
            document = Document.get(Integer.parseInt(args[0]))
            document.filename = args[1]
            document.description = args[2]

            // for files that were not updated, the binary id (args[3]) is -1
            if (binaryObject) {
                document.binaryObject = binaryObject
            }
            document.publicDocument = args[4].toBoolean()
            document.url = getDocumentUrl(document.binaryObject.id)
            document.publicUrl = getPublicDocumentUrl(args[1])

            document.save(true)
        } catch (NumberFormatException nfe) {
            log.error("Invalid Document Id!")
        }
        return document
    }

    def delete(String... args) {
        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")
        Document document = new Document()

        try {
            ids.forEach({ s ->
                if (s.length() > 0) {
                    document = Document.get(Integer.parseInt(s))

                    // now delete document
                    document.delete()
                }
            })

        } catch (NumberFormatException nfe) {
            log.error("Invalid Document Id!")
        }

        return document
    }

    def list() {
        def documents = []

        Document.findAll().forEach({d -> documents.add(d.toJsonMap())})

        return documents
    }

    private String getDocumentUrl(Long documentId) {

        String baseUrl = TBRProperties.getBaseUrl()

        StringBuilder sb = new StringBuilder(baseUrl)
        sb.append('/')
        sb.append('documents')
        sb.append('/')
        sb.append(documentId)
        return sb.toString()
    }

    private String getPublicDocumentUrl(String filename)  {
        return TBRProperties.getPublicDocumentApi()+'/'+filename
    }
}
