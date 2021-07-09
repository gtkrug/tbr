package tm.binding.registry

class PublicDocument {
    String filename
    String url
    String description
    Date   dateCreated

    PublicDocument(String name, String url, String description, Date createdDate)  {
        this.filename = name
        this.url = url
        this.description = description
        this.dateCreated = createdDate
    }
}