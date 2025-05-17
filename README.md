# Content-Aware-Image-Resizer-Java-Implementation-
The SeamCarver API 1) finds vertical and horizontal seams and 2) removes the vertical and horizontal seams of a picture 

## Constructor Summary 
   SeamCarver(Picture picture) - creates a seam carver object based on the given picture

## Method Summary (Modifier and Type, Method Name and Arguments)
   Picture picture() - returns the current picture 

   int width() - returns the width of the current picture
   
   public int height() - returns the height of the current picture

   double energy(int x, int y) - returns the energy of pixel at column x and row y

   int[] findHorizontalSeam() returns the sequence of indices for the horizontal seam

   int[] findVerticalSeam() - returns the sequence of indices for the vertical seam

   void removeHorizontalSeam(int[] seam) - removes horizontal seam from the current picture

   void removeVerticalSeam(int[] seam) - removes vertical seam from the current picture
