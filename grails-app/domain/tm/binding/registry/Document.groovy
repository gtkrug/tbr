package tm.binding.registry

class Document {

    int id
    String filename
    String url
    String publicUrl
    String description
    BinaryObject binaryObject
    Date dateCreated
    Boolean publicDocument = Boolean.TRUE

    static constraints = {
        filename(nullable: false, blank: false, maxSize: 255)
        url(nullable: false, blank: false, maxSize: 255)
        publicUrl(nullable: false, blank: false, maxSize: 255)
        description(nullable: true, blank: false, maxSize: 65535)
        binaryObject(nullable: true)
    }

    static mapping = {
        table(name:'documents')
        binaryObject(column: 'binary_object_ref')
        filename(column: 'file_name')
        description(type:'text', column: 'description')
        publicDocument(nullable: false)
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                filename: filename,
                url: url,
                publicUrl: publicUrl,
                binaryObjectId: binaryObject.id,
                description: description,
                dateCreated: dateCreated,
                publicDocument: publicDocument
        ]
        return json;
    }

}//end Documents
