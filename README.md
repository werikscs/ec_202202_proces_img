# Digital Image Processing — ImageJ plugins

Coursework for the Digital Image Processing class in my Computer Engineering degree (IFF, 2023).
Each topic of the syllabus is implemented **from scratch** as an ImageJ plugin in Java, rather than
called from an existing library.

The plugins live in [`Plugins_/src/`](Plugins_/src/) — that's the whole point of the repository. The
`IJ/` folder is a vendored copy of ImageJ so the project runs straight from Eclipse.

**Setup:** this project is built on my own template,
[image_processing_with_imagej](https://github.com/werikscs/image_processing_with_imagej) — see that
repository for how to import the project into Eclipse and how to create, run and remove plugins.

---

## Plugins

### Color and channels

| Plugin | What it does |
|---|---|
| `SplitChannels_` | Splits an RGB image into its three channels as separate grayscale images |
| `MergeChannels_` | Merges three grayscale channels back into a single RGB image, closing the source windows |
| `SplittedChannelsLUT_` | Applies a look-up table to the split channels, so each one displays in its own color |
| `RGBToGrayDialog_` | Converts RGB to grayscale through a dialog, producing an 8-bit image |

### Point operations

| Plugin | What it does |
|---|---|
| `PontoAPontoOperations_` | Brightness, contrast, solarization and desaturation — operations applied pixel by pixel, independent of neighbors |

Worth a note: these four transforms differ only in the function applied to each pixel, so the plugin was
refactored to take that function as a parameter using **Java functional interfaces**. Adding a new point
operation means writing the function, not another copy of the traversal loop.

### Histogram

| Plugin | What it does |
|---|---|
| `Histogram_` | Computes and plots the intensity histogram, with histogram expansion (contrast stretching) and an interactive contrast slider |

### Filters

| Plugin | What it does |
|---|---|
| `LinearFIlters_` | Convolution filters with a GUI for choosing the kernel: low-pass band, high-pass, and a directional high-pass for north-facing edges |
| `NonLinearFilters_` | Neighborhood filters whose output isn't a linear combination of the input — the rank-based family |
| `MorphologicalOperations_` | Morphological operations on binary images |

### Regions

| Plugin | What it does |
|---|---|
| `CreateImagesFromROI_` | Extracts the selected region of interest into a new image |

`Utils.java` holds the helpers shared across plugins.

---

## Structure

```
Plugins_/src/     the plugins — the actual work in this repository
IJ/               vendored ImageJ source, so the project runs from Eclipse
images/           test images
tools.jar         required on the build path (see the template's setup guide)
```

---

## Notes

Built with Eclipse IDE 2022-09 on Pop!_OS 20.04. The `IJ/bin` folder ships compiled classes on purpose,
so the project runs right after import without a build step.

The `LinearFIlters_` filename carries a typo (`FIlters`) that stuck around because renaming the file
means renaming the class ImageJ loads.
