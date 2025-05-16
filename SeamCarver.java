import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    // is the underlying picture object which is being transformed
    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        validate(picture);
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        Picture pictureCopy = new Picture(picture);
        return pictureCopy;

    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validate(x, y, width(), height());
        return (Math.sqrt(squareOfXGrad(x, y) + squareOfYGrad(x, y)));
    }

    // computes the square of the energy of the col coordinate of a pixel
    private double squareOfXGrad(int x, int y) {
        int colorRight;
        int colorLeft;
        if (x == picture.width() - 1) colorRight = picture.getARGB(0, y);
        else colorRight = picture.getARGB(x + 1, y);
        if (x == 0) colorLeft = picture.getARGB(picture.width() - 1, y);
        else colorLeft = picture.getARGB(x - 1, y);

        return rgbSquared(colorLeft, colorRight);


    }

    // computes the square of the energy of the row coordinate of a pixel
    private double squareOfYGrad(int x, int y) {
        int colorBottom;
        int colorTop;
        if (y >= height() - 1) colorBottom = picture.getARGB(x, 0);
        else colorBottom = picture.getARGB(x, y + 1);
        if (y <= 0) colorTop = picture.getARGB(x, height() - 1);
        else colorTop = picture.getARGB(x, y - 1);

        return rgbSquared(colorBottom, colorTop);

    }

    // takes a 32 bit integer representation of a color and returns
    // the addition of red value squared, blue value squared, and green value
    // squared
    private double rgbSquared(int color1, int color2) {
        int red1 = (color1 >> 16) & 0xFF;
        int red2 = (color2 >> 16) & 0xFF;
        int red = red1 - red2;
        int green1 = (color1 >> 8) & 0xFF;
        int green2 = (color2 >> 8) & 0xFF;
        int green = green1 - green2;
        int blue1 = (color1) & 0xFF;
        int blue2 = (color2) & 0xFF;
        int blue = blue1 - blue2;
        return (Math.pow(red, 2) + Math.pow(green, 2) + Math.pow(blue, 2));

    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Picture oldPic = picture;
        picture = tranpose(picture);
        int[] horizontalSeams = findVerticalSeam();
        picture = oldPic;
        return horizontalSeams;
    }

    // tranposes a picture
    private Picture tranpose(Picture pic) {
        Picture transposedPic = new Picture(pic.height(), pic.width());
        for (int i = 0; i < pic.width(); i++) {
            for (int j = 0; j < pic.height(); j++) {
                int color = pic.getARGB(i, j);
                transposedPic.setARGB(j, i, color);
            }
        }
        return transposedPic;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        double[][] distTo = new double[height()][width()];

        int[] edgeFrom = new int[width() * height()];

        int lastMinPixel = findVerticalSeam(distTo, edgeFrom);

        int[] verticalSeam = new int[height()];

        verticalSeam[height() - 1] = lastMinPixel % width();
        for (int i = height() - 2; i >= 0; i--) {
            verticalSeam[i] = edgeFrom[lastMinPixel] % width();
            lastMinPixel = edgeFrom[lastMinPixel];
        }
        return verticalSeam;
    }

    // assigns each pixel with it's closest distance to the top row and assigns
    // to each pixel which pixel came before it in the path from the top row
    // to said pixel. Additionally the function returns the pixel in the last
    // row with the shortest path to the top row
    private int findVerticalSeam(double[][] distTo, int[] edgeFrom) {
        int lastMinPixel = 0;
        double lastMinPixelEnergy = Double.POSITIVE_INFINITY;
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                distTo[j][i] = Double.POSITIVE_INFINITY;
                if (j == 0) {
                    distTo[j][i] = energy(i, j);
                    edgeFrom[i] = i;
                }
                else {
                    int min = width() * (j - 1) + i;
                    distTo[j][i] = distTo[j - 1][i];
                    if (i > 0) {
                        if (distTo[j][i] >= distTo[j - 1][i - 1]) {
                            distTo[j][i] = distTo[j - 1][i - 1];
                            min = width() * (j - 1) + (i - 1);
                        }

                    }
                    if (i < width() - 1) {
                        if (distTo[j][i] > distTo[j - 1][i + 1]) {
                            distTo[j][i] = distTo[j - 1][i + 1];
                            min = width() * (j - 1) + (i + 1);
                        }
                    }

                    distTo[j][i] += energy(i, j);
                    edgeFrom[width() * j + i] = min;
                }
                if (j == height() - 1) {
                    if (distTo[j][i] < lastMinPixelEnergy) {
                        lastMinPixel = width() * j + i;
                        lastMinPixelEnergy = distTo[j][i];
                    }
                }
            }
        }
        return lastMinPixel;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        picture = tranpose(picture);
        removeVerticalSeam(seam);
        picture = tranpose(picture);

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validate(seam, height(), width());
        Picture newPic = new Picture(width() - 1, height());
        for (int j = 0; j < height(); j++) {
            int newPicColIndex = 0;
            for (int i = 0; i < width(); i++) {
                if (i != seam[j]) {
                    int color = picture.getARGB(i, j);
                    newPic.setARGB(newPicColIndex, j, color);
                    newPicColIndex++;
                }
            }
        }
        picture = newPic;
    }


    // exception thrower for energy()
    private void validate(int col, int row, int width, int height) {
        if (col >= width || col < 0 || row >= height || row < 0)
            throw new IllegalArgumentException("Invalid Coordinates");
    }

    // exception thrower for removeVerticalSeam() and removeHorizontalSeam()
    private void validate(int[] seam, int mainDimension, int subDimension) {
        if (subDimension == 1) throw new IllegalArgumentException(
                "Invalid Seam");
        if (seam == null) throw new IllegalArgumentException(
                "Invalid Seam");
        if (seam.length != mainDimension) throw new IllegalArgumentException(
                "Invalid Seam");
        for (int i = 0; i < mainDimension; i++) {
            if (i != (mainDimension - 1)) {
                if (Math.abs(seam[i] - seam[i + 1]) > 1)
                    throw new IllegalArgumentException("Invalid Seam");
            }
            if (seam[i] >= subDimension || seam[i] < 0)
                throw new IllegalArgumentException("Invalid Seam");
        }
    }

    // exception thrower for constructor
    private void validate(Picture pic) {
        if (pic == null) throw new IllegalArgumentException(
                "Invalid Constructor");

    }

    //  unit testing
    public static void main(String[] args) {

        Picture pic = new Picture("4x6.png");
        SeamCarver seams = new SeamCarver(pic);
        StdOut.println(seams.picture());


    }

}

