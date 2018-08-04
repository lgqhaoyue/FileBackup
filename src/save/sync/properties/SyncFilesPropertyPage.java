package save.sync.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class SyncFilesPropertyPage extends PropertyPage {

	private static final String CURRENT_PATH_TITLE = "Current project Path:";
	private static final String Dest_PATH_TITLE = "Destination Path:";
	//private static final String current_path = "";
	private static final String destination_path = "";
	public static  final QualifiedName destPathQualified = new QualifiedName("/sync_dest_path","sync_dest_path");

	private static final int TEXT_FIELD_WIDTH = 100;

	private Text destText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public SyncFilesPropertyPage() {
		super();
	}

	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		String location = ((IResource) getElement()).getLocation().toString();

		// Label for owner field
		Label destLabel = new Label(composite, SWT.NONE);
		destLabel.setText(CURRENT_PATH_TITLE);

		// Owner text field
		Text currentPathText = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		currentPathText.setText(location);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		currentPathText.setLayoutData(gd);

	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for owner field
		Label destLabel = new Label(composite, SWT.NONE);
		destLabel.setText(Dest_PATH_TITLE);

		// Owner text field
		destText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		destText.setLayoutData(gd);

		// Populate owner text field
		try {
			String dest =
				((IResource) getElement()).getPersistentProperty(
						destPathQualified);
			destText.setText((dest != null) ? dest : destination_path);
		} catch (CoreException e) {
			destText.setText(destination_path);
		}
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		super.performDefaults();
		// Populate the owner text field with the default value
		destText.setText(destination_path);
	}
	
	public boolean performOk() {
		
		String property  = destText.getText();
		if(property.indexOf('\\')  >  0)
		{
			destText.setText("can  not contain '\\',please replace by  '/'");
			return false;
		}
		
		// store the value in the owner text field
		try {
			((IResource) getElement()).setPersistentProperty(
			    destPathQualified,
				destText.getText());
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}