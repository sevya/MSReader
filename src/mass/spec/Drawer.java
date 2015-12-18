package mass.spec;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;


public class Drawer extends JPanel implements ActionListener {
    private final String parentSequence;
    private final List<String> peptideSequences;
    private final List<Color> colors;
    private final JMenuBar menuBar;
    private final JMenu menu;
    private final JMenuItem saveItem;
    private final JFrame mainFrame;
    
    private boolean imageRendered;
    private int renderCount;
    
    public Drawer( String parent, List<String> peptides, List<Color> cols ) {
        parentSequence = parent.replaceAll("\\s+", "");
        peptideSequences = peptides;
        colors = cols;
        mainFrame = new JFrame("Exchange map");
        mainFrame.setPreferredSize(new Dimension(8*50+50, 600));
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().add(this, BorderLayout.CENTER);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
        
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menuBar.add( menu );
        saveItem = new JMenuItem("Save image");
        menu.add(saveItem);
        saveItem.addActionListener(this);

        mainFrame.pack();  
        
        mainFrame.setJMenuBar(menuBar);
        
        imageRendered = false;
        renderCount = 0;

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File (Utils.osjoin(System.getProperty("user.home"), "Documents")));
//        fileChooser.setCurrentDirectory(MSReader.getInstance().documents);
        fileChooser.setSelectedFile(new File(""));
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        File f = fileChooser.getSelectedFile();
        Container c = mainFrame.getContentPane();
        BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        c.paint(im.getGraphics());
        if ( !f.toString().substring(f.toString().length()-3).toLowerCase().equals("png") ) {
            f = new File(f.toString()+".png");
        }
        try {
            ImageIO.write(im, "PNG", f);
        } catch ( IOException exc ) {
            Utils.showErrorMessage("Unable to save image");
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
//        if ( imageRendered ) return;
//        if ( renderCount > 2 ) return;
        super.paintComponent(g); // always include this
        setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D)g;
        Font defaultFont = new Font("Courier New", Font.PLAIN, 14);
        int startX = 25;
        int startY = 25;

        int letterWidth = (int) defaultFont.getStringBounds("A", new FontRenderContext(defaultFont.getTransform(), false, false)).getBounds().getWidth();
        int letterHeight = (int) defaultFont.getStringBounds("A", new FontRenderContext(defaultFont.getTransform(), false, false)).getBounds().getHeight();
        int dropY = 10;

        g2.setFont(defaultFont);
        
        int aaPerLine = 50;
        int lineDrop = 10;
        drawSequence( g2, parentSequence, aaPerLine, startX, startY, dropY*lineDrop );
        
        List<PeptideLine> neighbors = new ArrayList();
        for ( int i = 0; i < peptideSequences.size(); ++i ) {
            PeptideLine pept = new PeptideLine(parentSequence, peptideSequences.get(i), 
                aaPerLine, dropY, startX, startY, letterWidth, lineDrop, 
                neighbors, true, colors.get(i));
            pept.draw(g2);
        }
        
        setSize(new Dimension(letterWidth*aaPerLine+2*startX, 600));
        
        imageRendered = true;
        renderCount++;
    }
    
    private void drawSequence( Graphics g, String sequence, int aaPerLine, 
            int startX, int startY, int dropY ) {
        for ( int i = 0; i < sequence.length(); i+=aaPerLine ) {
            int lower = i;
            int upper = ( i+aaPerLine > sequence.length() ) ? sequence.length() : i+aaPerLine;
            g.drawString(sequence.substring(lower, upper), startX, startY+dropY*(i/aaPerLine) );
        }
    }   
    
    public static void drawExchangeMap( String parentSequence, 
        List<String> peptideSequences, List<Color> colors ) {
        Drawer drawer = new Drawer( parentSequence, peptideSequences, colors );
    }
    
    public static void main ( String[] args ) {
        String parent = "EVQLVESGGGLIQPGGSLRLSCAVSGFTVSSKYMTWVRQAPGKGLEWVSVIYGGGS\n" +
"TYYADSVVGRFTISRDNSKNTLYLQMNSLRAEDTAVYYCASRLGVRATTGDLDYWGQ\n" +
"GTLVTVSSASTKG\n" +
"DIQMTQSPSTLSASVGDRVTITCRASQSISSWLAWYQQKPGKAPKLLIYDASSLESGV\n" +
"PSRFSGSGSGTEFTLTISSLQPDDFATYYCQQYNTYSWWTFGQGTKVDIK";
        List<String> peptides = new ArrayList();
        peptides.add("TVSSKYMTWVRQAPGKGLE");
        peptides.add("FTVSSKYMT");
        peptides.add("DYWGQGTL");
        peptides.add("GVRATTGDLDY");
        peptides.add("LIYDASSL");
        peptides.add("LIYDASSLESGVPSRFSGSGSGTE");
        peptides.add("LIYDASSLESGVPSRFSGSGSGTEF");
        peptides.add("IYGGGSTY");
        peptides.add("IYGGGSTYYADSVVGRF");
        
        peptides.add("YYCASRL");
        peptides.add("LAWYQQKPGKAPKL");
        peptides.add("LAWYQQKPGKAPKLLIYDASSL");
        peptides.add("YQQKPGKAPKL");
        peptides.add("YADSVVGRF");
        peptides.add("YADSVVGRFTISRDNSKNTLY");

        Color[] colArray = new Color[] {Color.red, Color.blue, Color.magenta, Color.yellow, Color.black, Color.cyan};
        
        List<Color> colors = new ArrayList();
        for ( String s : peptides ) {
            colors.add( colArray[ new Random().nextInt(colArray.length)] );
        }

        Drawer.drawExchangeMap( parent, peptides, colors );
    }
}

final class PeptideLine  {
        final int aaPerLine_, dropY_, startX_, startY_, letterWidth_, lineDrop_;
        int x1_, y1_, x2_, y2_, x21_, x22_, y21_, y22_;
        int offset_;
        final String parentSequence_, peptideSequence_;
        int startLocation, endLocation, dropNumber, dropNumber2;
        boolean crossesLine, drawNubs_;
        List<PeptideLine> neighbors_;
        final Color color_;
        
        public PeptideLine( String parentSequence, String peptideSequence, 
                int aaPerLine, int dropY, 
                int startX, int startY, int letterWidth, int lineDrop,
                List<PeptideLine> neighbors, boolean drawNubs, Color color ) {
            parentSequence_ = parentSequence;
            peptideSequence_ = peptideSequence;
            aaPerLine_ = aaPerLine;
            startX_ = startX;
            startY_ = startY;
            dropY_ = dropY;
            letterWidth_ = letterWidth;
            lineDrop_ = lineDrop;
            neighbors.add( this );
            neighbors_ = neighbors;
//            neighbors_.add(this);
            calculateParameters();
            calculateOffset();
            drawNubs_ = drawNubs;
            color_ = color;
        }
        
        public void calculateOffset() {
            offset_ = 0;
            for ( PeptideLine other : neighbors_ ) {
                if ( this.overlaps( other ) && this.offset_==other.offset_ ) {
                    offset_ += 1;
                }
            }
            y1_ += offset_*dropY_;
            y2_ += offset_*dropY_;
            if ( crossesLine ) {
                y21_ += offset_*dropY_;
                y22_ += offset_*dropY_;
            }
        }
        
        public void calculateParameters() {
            /// find location in sequence
            startLocation = parentSequence_.indexOf(peptideSequence_);
            /// if peptide sequence is not in parent sequence
            if ( startLocation == -1 ) return;
            endLocation = startLocation + peptideSequence_.length();
            dropNumber = Math.floorDiv( startLocation, aaPerLine_ )+1;
            crossesLine = Math.floorDiv( startLocation, aaPerLine_ ) != 
                    Math.floorDiv( endLocation, aaPerLine_ );
            int endY = dropY_/2;

            if ( crossesLine ) {
                int start1, end1, start2, end2;
                start1 = startLocation;
                start2 = startLocation;
                while (Math.floorDiv( start1, aaPerLine_ ) == Math.floorDiv( start2, aaPerLine_ )) start2++;
                end1 = start2 - 1;
                end2 = start2 + peptideSequence_.length() - (end1-start1);
                // draw first line
                {
                    dropNumber = (lineDrop_*Math.floorDiv( start1, aaPerLine_ ))+1;
                    x1_ = startX_ + (start1%aaPerLine_)*letterWidth_;
                    x2_ = startX_ + aaPerLine_*letterWidth_;
                    y1_ = startY_ + dropNumber*dropY_ - endY;
                    y2_ = startY_ + dropNumber*dropY_ + endY;
                }
                // draw second line
                {
                    dropNumber2 = (lineDrop_*Math.floorDiv( start2, aaPerLine_ ))+1;
                    x21_ = startX_;
                    x22_ = startX_ + (end2%aaPerLine_)*letterWidth_;
                    y21_ = startY_ + dropNumber2*dropY_ - endY;
                    y22_ = startY_ + dropNumber2*dropY_ + endY;
                }
            } else {
                dropNumber = (lineDrop_*Math.floorDiv( startLocation, aaPerLine_ ))+1;
                x1_ = startX_ + (startLocation%aaPerLine_)*letterWidth_;
                x2_ = startX_ + (endLocation%aaPerLine_)*letterWidth_;
                y1_ = startY_ + dropNumber*dropY_ - endY;
                y2_ = startY_ + dropNumber*dropY_ + endY;
            }
        }
        
        public void draw ( Graphics2D g ) {
            g.setColor( color_ );
            if ( crossesLine ) {
                g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                /// draw main line
                g.drawLine( x1_, (y1_+y2_)/2, x2_, (y1_+y2_)/2 );
                g.drawLine( x21_, (y21_+y22_)/2, x22_, (y21_+y22_)/2 );
                
                if ( drawNubs_ ) {
                    // draw nubs at the end
//                    g.setColor( Color.black );
                    g.setStroke(new BasicStroke(1));
                    g.drawLine(x1_, y1_, x1_, y2_);
                    g.drawLine(x22_, y21_, x22_, y22_);
                }
            } else {
                g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                g.drawLine( x1_, (y1_+y2_)/2, x2_, (y1_+y2_)/2 );
                if ( drawNubs_ ) {
                    /// draw nubs at the end
//                    g.setColor( Color.black );
                    g.setStroke(new BasicStroke(1));
                    g.drawLine(x1_, y1_, x1_, y2_);
                    g.drawLine(x2_, y1_, x2_, y2_);
                }
            }
        }
        
        public boolean overlaps( PeptideLine other ) {
            if ( this == other ) return false;
            
            if ( this.crossesLine && other.crossesLine ) {
                return overlaps( this.x1_, this.x2_, this.y1_, this.y2_,
                        other.x1_, other.x2_, other.y1_, other.y2_ ) || 
                overlaps( this.x21_, this.x22_, this.y21_, this.y22_,
                        other.x1_, other.x2_, other.y1_, other.y2_ ) || 
                overlaps( this.x1_, this.x2_, this.y1_, this.y2_,
                        other.x21_, other.x22_, other.y21_, other.y22_ ) || 
                overlaps( this.x21_, this.x22_, this.y21_, this.y22_,
                        other.x21_, other.x22_, other.y21_, other.y22_ ) ;
            } 
            
            else if ( this.crossesLine ) {
                return overlaps( this.x1_, this.x2_, this.y1_, this.y2_,
                        other.x1_, other.x2_, other.y1_, other.y2_ ) || 
                overlaps( this.x21_, this.x22_, this.y21_, this.y22_,
                        other.x1_, other.x2_, other.y1_, other.y2_ );
            } 
            
            else if ( other.crossesLine ) {
                return overlaps( this.x1_, this.x2_, this.y1_, this.y2_,
                        other.x2_, other.x2_, other.y2_, other.y2_ ) || 
                overlaps( this.x1_, this.x2_, this.y1_, this.y2_,
                        other.x21_, other.x22_, other.y21_, other.y22_ ) ;
            } 
            
            else {
                return overlaps( this.x1_, this.x2_, this.y1_, this.y2_,
                        other.x1_, other.x2_, other.y1_, other.y2_ );
            }
        }
        
        public boolean overlaps( int xa1, int xa2, int ya1, int ya2, 
                                 int xb1, int xb2, int yb1, int yb2 ) {
            return xa1 < xb2 && xa2 > xb1 && 
                    ya1 < yb2 && yb2 > ya1;
        }
    }
