
PANOPLY

  panoply \PAN-uh-plee\, noun: 1. A splendid or impressive array. [...]


INTRODUCTION

  Panoply is a Java application that allows the user to make plots of data from netCDF, HDF,
  and GRIB datasets. Although its strength is in making longitude-latitude (map) plots,
  it can also make other types of georeferenced plots, including keograms and Hovmoeller
  diagrams; general 2D color plots; and line plots. Data arrays may be "sliced" from larger
  multidimensional arrays (aka, variables). A large selection of global map projections is
  available for lon-lat figures. Plot images may be saved to disk in bitmap graphic formats
  or in PDF.

  Panoply requires that a Java 8 (or later) Runtime Environment be installed on your computer.
  If not already present, you will need to download and install a JRE from Java.com. For more
  info about installing Java on Windows, see
  https://java.com/en/download/help/windows_manual_download.xml


DOWNLOADING

  The current version of Panoply, along with other information about the application, may
  always be found at
  https://www.giss.nasa.gov/tools/panoply/


INSTALLING AND RUNNING THE PANOPLY FOR WINDOWS PACKAGE

  The Panoply package for Windows comes as a zipped archive. You _must_ manually extract
  this archive. Some versions of Windows allow you to run programs from within a zipped
  archive without extraction. If you try this with Panoply, it won't work.

  The Panoply package for Windows should, after uncompression, include the following items:

  - Panoply.exe application.
  - Application code files in a folder (sub-directory) called "jars".
  - This README file.
  
  To run Panoply, double-click on the Panoply.exe application.


JAR FILES

  The sub-directory called jars _must_ remain in the same directory as the panoply.sh
  script, and all the "jar" files it holds must remain in the jars sub-directory. These files
  contain the Panoply application code and (re)moving any of them will break Panoply.


OTHER DOCUMENTATION

  More details about Panoply are available at:
  
    https://www.giss.nasa.gov/tools/panoply/
  
  There are additional installation notes at
  
    https://www.giss.nasa.gov/tools/panoply/download.html


COLOR TABLES AND CONTINENTS FILES

  Beginning with version 4, Panoply's "standard" selection of color tables and map overlays
  is built into the application. Additional color tables and map overlays may be opened for
  a single session or added to your favorites library for continued use. See the Panoply
  website for for such optional support files.


CONTACT

  Panoply was developed at the

  NASA Goddard Institute for Space Studies
  2880 Broadway, New York, NY 10025 USA

  Please send bug reports, etc., to Dr. Robert Schmunk at robert.b.schmunk@nasa.gov.


ACKNOWLEDGMENTS

  Panoply uses Java classes and libraries written by several third-party organizations. A
  complete list, with links to pertinent websites containing license information and source
  code, may be accessed via Panoply's Help menu or on the Panoply website.
