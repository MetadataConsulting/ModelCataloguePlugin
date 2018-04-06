package org.modelcatalogue.core.assets

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1768')
@Title('Check that a viewer is able to download an asset')
@Narrative('''
- Login to model catalogue As supervisor
- Select a draft data model that Curator has access too| Directed to data model page
- Navigate to the assets tab | 'Active Assets' page is open
- Press the green plus button to add a new asset | Create Asset Wizard pop-up is open
- Populate form with name, description and brown files to upload an asset . Click save. | New Asset is created
- Click settings menu, top right and select Data Model ACL from the drop down | redirected to the Data Model ACL page . Data Model Permissions is the title
- Select data model that you created asset within from list | Directed to data model permissions page. Data model name is the title
- Select User from drop-down and grant the User read only rights over the data model | User's name is added to list of users with rights for this data model
- Log out as Supervisor | Supervisor is logged out
- Log in as Curator | Curator is logged in
- Select the data model that you are authorised to view, in which supervisor created the asset. | directed to data model page
- Navigate to the tree view and click on the Asset Tags | Active Assets is displayed
- Select an asset | taken to the asset page with asset name as title.
- Navigate to the right side and click on the download button | Browser Download pop - up appears.
- Check that the file has downloaded
''')
class UserIsAbleToDownloadAnAssetSpec extends Specification {
}
