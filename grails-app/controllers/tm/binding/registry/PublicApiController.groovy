package tm.binding.registry

import grails.converters.JSON
import grails.converters.XML
import grails.plugin.springsecurity.SpringSecurityUtils
import tm.binding.registry.util.TBRProperties

import javax.servlet.ServletException

class PublicApiController {

    final String STATUS = "/status/"

    def index() {
    }

    def documents() {
    }

    def signingCertificates() {
    }

    /**
     * return pdf document when requested by name
     * @return
     */
    def pdfByName() {
        log.debug("Viewing PDF document: ${params.name}...")

        Document doc = Document.findByFilename(params.name)
        if( doc == null) {
            render (status:404, text: "No such document named: ${params.name}")
            return
        }

        // get the document associated to the binary id
        BinaryObject binaryObject = BinaryObject.findById(doc.binaryObjectId);
        if( !binaryObject ) {
            throw new ServletException("No such binary object: ${doc.binaryObjectId}")
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

            log.info("Rendering binary data...")

            return render(file: fileInputStream, contentType: mimeType);
        } else {
            return redirect(controller:'error', action:'notAuthorized401')
        }
    }

    /**
     * find public documents
     * @return
     */
    def findDocs() {
        log.info("Finding documents ...")
        List<PublicDocument> documents = new ArrayList<>()
        if(params.id != null)  {
            try  {
                Integer id = Integer.parseInt(params.id)
                Document.findAllByIdAndPublicDocument(id, true).forEach(
                        {d -> documents.add(new PublicDocument(d.filename, generateDocumentUrl(d.filename), d.description, d.dateCreated))
                        })
            } catch (NumberFormatException nfe)  {
                if(params.id == "ALL")  {
                    Document.findAllByPublicDocument(true).forEach(
                            {d -> documents.add(new PublicDocument(d.filename, generateDocumentUrl(d.filename), d.description, d.dateCreated))
                            })
                } else {
                    Document.findAllByFilenameLikeAndPublicDocument("%"+params.id+"%", true).forEach(
                            {d -> documents.add(new PublicDocument(d.filename, generateDocumentUrl(d.filename), d.description, d.dateCreated))
                            })
                }
            }
        }
        
        withFormat {
            json {
                render documents as JSON
            }
            xml {
                render documents as XML
            }
        }
    }

    /**
     * find public documents
     * @return
     */
    def findSigningCertificates() {
        log.info("Finding signing certificates ...")

        List<PublicSigningCertificate> certificates = new ArrayList<>()
        if(params.id != null)  {
            try  {
                Integer id = Integer.parseInt(params.id)
                SigningCertificate.findAllById(id, true).forEach(
                        {c -> certificates.add(new PublicSigningCertificate(c.certificatePublicUrl, c.defaultCertificate))
                        })
            } catch (NumberFormatException nfe)  {
                if(params.id == "ALL")  {
                    SigningCertificate.findAll().forEach(
                            {c -> certificates.add(new PublicSigningCertificate(c.certificatePublicUrl, c.defaultCertificate))
                            })
                }
            }
        }

        withFormat {
            json {
                render certificates as JSON
            }
            xml {
                render certificates as XML
            }
        }
    }

    /**
     * generates the TSR url from the trustmark id
     * @param trustmarkId
     * @return
     */
    private String generateDocumentUrl(String docName)  {
        return TBRProperties.getPublicDocumentApi()+'/'+docName
    }
}
