
package mass.spec;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtensionFilter extends FileFilter {
    private String extensions[];
    private String description;

    static final FileFilter hdxfilter = new ExtensionFilter("HDX files", new String[] {".HDX", ".hdx"});
    static final FileFilter cdffilter = new ExtensionFilter("netCDF file", new String[] {".CDF", ".cdf"});
    static final FileFilter pepfilter = new ExtensionFilter("Peptide list", new String[] {".PEP", ".pep"});
    static final FileFilter pdbfilter = new ExtensionFilter("PDB file", new String[] {".PDB", ".pdb"});
    static final FileFilter txtfilter = new ExtensionFilter("Text file", new String[] {".TXT", ".txt"});
    static final FileFilter mzmlfilter = new ExtensionFilter("MzML file", new String[] {".MZML", ".mzml", ".mzML"});
    static final FileFilter msreadablefilter = new ExtensionFilter("Readable files", new String[] {".MZML", ".mzml", ".mzML", ".CDF", ".cdf"});
    
    public ExtensionFilter(String description, String extension) {
      this(description, new String[] { extension });
    }

    public ExtensionFilter(String description, String extensions[]) {
      this.description = description;
      this.extensions = (String[]) extensions.clone();
    }
    
    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      int count = extensions.length;
      String path = file.getAbsolutePath();
      for (int i = 0; i < count; i++) {
        String ext = extensions[i];
        if (path.endsWith(ext)
            && (path.charAt(path.length() - ext.length()) == '.')) {
          return true;
        }
      }
      return false;
    }

    public String getDescription() {
      return (description == null ? extensions[0] : description);
    }

    }
